(ns readerss.file-system-test
  (:require [clojure.test :refer :all]
            [readerss.file-system :as fs]
            [clojure.java.io :as io]))

(deftest is-file?
  (testing "a valid file"
    (is (= true (fs/is-file? (io/file "feeds-sample.txt")))))
  (testing "a invalid file"
    (is (= false (fs/is-file? (io/file "not-valid-file.txt"))))))
