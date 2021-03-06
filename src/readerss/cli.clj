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
       (filterv identity)                                    ; Remove nil
       dedupe                                               ; Remove duplicated paths
       (mapv io/file)                                        ; Convert to file
       (filterv fs/is-file?)                                 ; Check if it is a file
       (mapv slurp)                                         ; Read files contents
       (mapcat logic/split)                                 ; Split and joint lines
       dedupe                                               ; Remove url duplicates
       doall))                                              ; Force lazy sequence completion

(defn -main
  [& args]
  (let [randnumb (rand 50000)
        paths    (or (not-empty args) [feed-file-path])]
    (println "Using:" randnumb "as random number for while true sleep delay increase/decrease as you wish")
    (println "Running for feed files:" paths)
    (core/execute (load-url-list paths))))
