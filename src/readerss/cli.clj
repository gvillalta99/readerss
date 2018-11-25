(ns readerss.cli
  (:gen-class)
  (:require [clojure.java.io :as io]
            [readerss.core :as core]
            [readerss.file-system :as fs]
            [readerss.logic :as logic]))

(def feed-file-path "feeds-sample.txt")

(defn load-url-list
  "For a list of paths.
   Reads every file and extract every line with an url."
  [file-paths]
  (->> file-paths
       (filter identity)                                    ; Remove nil
       dedupe                                               ; Remove duplicated paths
       (map io/file)                                        ; Convert to file
       (filter fs/is-file?)                                 ; Check if it is a file
       (pmap slurp)                                         ; Read files contents
       (mapcat logic/split)                                 ; Split and joint lines
       dedupe                                               ; Remove url duplicates
       doall))                                              ; Force lazy sequence completion

(defn -main
  [& args]
  (let [randnumb (rand 50000)]
    (println "Using:" randnumb "as random number for while true sleep delay increase/decrease as you wish")
    (println (core/execute (load-url-list [feed-file-path]))))
  #_(while true (Thread/sleep randnumb) (execute)))
