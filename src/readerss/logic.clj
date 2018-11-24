(ns readerss.logic
  (:require [digest]
            [clojure.string :as string])
  (:import [org.apache.commons.validator UrlValidator]))

(defn valid-url?
  [url-str]
  (let [validator (UrlValidator.)]
    (.isValid validator url-str)))

(defn digest
  [url-str]
  (digest/sha-256 url-str))

(defn split [s]
  (string/split s #"\n"))
