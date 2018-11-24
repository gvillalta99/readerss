(ns readerss.core
  (:require [clj-http.client :as client
            [clojure.java.io :as io]
            [pantomime.extract :as extract]
            [readerss.logic :as logic]
            [remus :refer [parse-url parse-file]]]))

(def last-acc-map (atom {}))

(def feedfile (io/file "feeds-sample.txt"))

(defn url-to-filename [url]
  (str "resources/"(logic/digest url)))

(defn savedisk [file url]
  (let [filez (extract/parse (io/input-stream file))]
    (cond
      (re-find #"pdf" (str (:content-type filez))) (spit (str (url-to-filename url) "-PDF-EXTRACTED") (:text filez))
      (re-find #"html" (str (:content-type filez))) (spit (str (url-to-filename url) "-HTML-EXTRACTED") (:text filez))
      :else (println "Sorry but i can't handle this type of file"))))

(defn create [url]
  (try
    (let [file (:body (client/get url {:as :stream}))]
      (savedisk file url))
    (catch Exception e (println "ERROR ON create" url))))

(defn fetcher [url]
  (if (logic/valid-url? url)
    (create url)
    (println "Not a valid url: " url)))

(defn parsefile [url]
  (let [pdffile  (.exists (io/as-file (str (url-to-filename url) "-PDF-EXTRACTED")))
        htmlfile (.exists (io/as-file (str (url-to-filename url) "-HTML-EXTRACTED")))]
    (if (or htmlfile pdffile)
      (do
        (println "URL:" url "already fetched"))
      (do
        (println "Fetching URL:" url)
        (fetcher url)))))

(defn extract-feeds [feed]
  (try
    (let [url-hashed (logic/digest feed)
          result (parse-url feed {:headers {"User-Agent" "Mozilla/5.0 (Macintosh; Intel Mac"}})
          status-code (:status (:response result))
          last-modified (:last-modified (:headers (:response result)))
          entries (map :link (:entries (:feed result)))]
      (swap! last-acc-map assoc url-hashed last-modified)
      (map parsefile entries))
    (catch Exception e (println "ERROR ON extract-feeds" e))))

(defn feeder [feed]
  (if (logic/valid-url? feed)
    (map extract-feeds [feed])
    (println "Not going to fetch feed: " feed "not url format")))

(defn execute []
  (str (map feeder (logic/split (slurp (java.io.FileReader. feedfile))))))

