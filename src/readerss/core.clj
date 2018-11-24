(ns readerss.core
 (:gen-class)
 (:require [clojure.java.io :as io]
           [readerss.logic :as logic]
           [pantomime.extract :as extract]
           [remus :refer [parse-url parse-file]]
           [clj-http.client :as client]))

(def last-acc-map (atom {}))

(def feedfile (io/file "feeds-sample.txt"))

(defn url-to-filename [url]
  (str "resources/"(logic/digest url)))

(defn savedisk [file url]
  (def filez (extract/parse (io/input-stream file)))
    (cond
      (re-find #"pdf" (str (:content-type filez))) (spit (str (url-to-filename url)"-PDF-EXTRACTED") (:text filez))
      (re-find #"html" (str (:content-type filez))) (spit (str (url-to-filename url)"-HTML-EXTRACTED") (:text filez))
      :else (println "Sorry but i can't handle this type of file")))

(defn create [url]
  (try
  (def file (:body (client/get url {:as :stream})))
    (savedisk file url)
    (catch Exception e (println "ERROR ON create" url))))

(defn fetcher [url]
  (if (logic/valid-url? url)
   (create url) 
   (println "Not a valid url: " url)))

(defn parsefile [url]
  (def pdffile (.exists (io/as-file (str (url-to-filename url)"-PDF-EXTRACTED"))))
  (def htmlfile (.exists (io/as-file (str (url-to-filename url)"-HTML-EXTRACTED"))))
  (if (or htmlfile pdffile) 
    (do
      (println "URL:" url "already fetched"))
    (do
      (println "Fetching URL:" url)
      (fetcher url))))

(defn extract-feeds [feed]
  (try
  (def url-hashed (logic/digest feed))
  (def result (parse-url feed {:headers {"User-Agent" "Mozilla/5.0 (Macintosh; Intel Mac"}}))
  (def status-code (:status (:response result)))
  (def last-modified (:last-modified (:headers (:response result))))
  (def entries (map :link (:entries (:feed result))))
  (swap! last-acc-map assoc url-hashed last-modified)
  (map parsefile entries)
  (catch Exception e (println "ERROR ON extract-feeds" e))))

(defn feeder [feed]
  (if (logic/valid-url? feed)
    (map extract-feeds [feed])
    (println "Not going to fetch feed: " feed "not url format")))

(defn execute []
  (str (map feeder (logic/split (slurp (java.io.FileReader. feedfile))))))

(defn -main
 [& args]
  (def randnumb (rand 50000))
  (println "Using:"randnumb "as random number for while true sleep delay increase/decrease as you wish")
  (execute)
  #_(while true (Thread/sleep randnumb) (execute)))

