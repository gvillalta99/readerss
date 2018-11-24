(ns readerss.cli
  (:gen-class)
  (:require [readerss.core :as core]))

(defn -main
  [& args]
  (let [randnumb (rand 50000)]
    (println "Using:" randnumb "as random number for while true sleep delay increase/decrease as you wish")
    (core/execute))
  #_(while true (Thread/sleep randnumb) (execute)))
