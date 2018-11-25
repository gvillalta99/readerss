(ns readerss.core
  (:require [clj-http.client :as client]
            [clojure.java.io :as io]
            [pantomime.extract :as extract]
            [readerss.file-system :as fs]
            [readerss.logic :as logic]
            [remus :refer [parse-url parse-file]]))

(def http-headers {:headers {"User-Agent" "Mozilla/5.0 (Macintosh; Intel Mac"}})

(defn url-str->internal                                     ; This will be the internal representation of a url
  [url-str]
  {:url/str    url-str
   :url/hashed (logic/digest url-str)
   :url/valid? (logic/is-url? url-str)})

(defn url->cached-entry
  [url-str]
  (let [base-filename (logic/url->base-filename url-str)
        html-file     (str base-filename "-HTML-EXTRACTED")
        pdf-file      (str base-filename "-PDF-EXTRACTED")]
    {:entry/url          url-str
     :cache/base         base-filename
     :cache/html         html-file
     :cache/html-exists? (fs/is-file? (io/file html-file))
     :cache/pdf          pdf-file
     :cache/pdf-exists?  (fs/is-file? (io/file pdf-file))}))

(defn with-feed-results
  [url]
  (try
    (let [http-result   (parse-url (:url/str url) http-headers)
          last-modified (-> http-result :response :headers :last-modified)
          status        (-> http-result :response :status)
          links         (->> http-result :feed :entries (mapv :link))]
      (assoc url
        :http/status status
        :http/last-modified last-modified
        :feed/links links))
    (catch Exception e
      (assoc url
        :http/error e))))

(defn validate-url
  [url]
  (if (:url/valid? url)
    url
    (println "Not going to fetch feed: " (:url-str url) " not url format")))

(defn cache-entry
  [{:keys [http/body cache/html cache/pdf entry/url] :as entry}]
  (cond
    (re-find #"pdf" (str (:content-type body)))
    (do
      (println "Caching" url "at" pdf)
      (spit pdf (:text body))
      (assoc entry :cache/pdf-exists? true))
    (re-find #"html" (str (:content-type body)))
    (do
      (println "Caching" url "at" html)
      (spit html (:text body))
      (assoc entry :cache/html-exists? true))
    :else
    (do
      (when body (println "Sorry, can't handle this type of file:" url))
      entry)))

(defn with-cached-feed-entries
  [url]
  (let [links (:feed/links url)]
    (assoc url
      :feed/entries (mapv url->cached-entry links))))

(defn fetch-feed-entry
  [url]
  (-> (client/get url {:as :stream})
      :body
      io/input-stream
      extract/parse))

(defn with-http-feed-entry
  [{:keys [cache/pdf-exists? cache/html-exists? entry/url] :as entry}]
  (if (or pdf-exists? html-exists?)
    (do
      (println "Already fetched URL:" url)
      entry)
    (do
      (println "Fetching URL:" url)
      (assoc entry
        :http/body (fetch-feed-entry url)))))

(defn with-feed-entries
  [url]
  (let [entries (:feed/entries url)]
    (assoc url
      :feed/entries (mapv with-http-feed-entry entries))))

(defn cache-feed-entries
  [{:keys [feed/entries] :as url}]
  (assoc url
    :feed/entries (mapv cache-entry entries)))

(defn execute [url-list]
  (->> url-list
       (map url-str->internal)
       (map validate-url)
       (filter identity)
       (pmap with-feed-results)
       (pmap with-cached-feed-entries)
       (pmap with-feed-entries)
       (pmap cache-feed-entries)
       doall))
