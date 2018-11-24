(ns readerss.cli
  (:gen-class)
  (:require [readerss.core :as core]))

(def feed-file-path "feeds-sample.txt")

(defn -main
  [& args]
  (let [randnumb (rand 50000)]
    (println "Using:" randnumb "as random number for while true sleep delay increase/decrease as you wish")
    (println (core/execute feed-file-path)))
  #_(while true (Thread/sleep randnumb) (execute)))
