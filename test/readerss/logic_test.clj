(ns readerss.logic-test
  (:require [clojure.test :refer :all]
            [readerss.logic :as logic]))

(deftest is-url?
  (testing "a valid url"
    (is (= true (logic/is-url? "http://google.com"))))
  (testing "a invalid url"
    (is (= false (logic/is-url? "google.com")))))

(deftest digest
  (testing "with a string"
    (is (= "aa2239c17609b21eba034c564af878f3eec8ce83ed0f2768597d2bc2fd4e4da5" (logic/digest "http://google.com"))))
  (testing "with nil"
    (is (= nil (logic/digest nil)))))

(deftest split
  (testing "with one line"
    (is (= ["single line"] (logic/split "single line"))))
  (testing "with multiple lines"
    (is (= ["multiple" "lines"] (logic/split "multiple\nlines"))))
  (testing "with empty string"
    (is (= [""] (logic/split ""))))
  (testing "with nil"
    (is (= nil (logic/digest nil)))))
