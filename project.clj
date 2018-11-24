(defproject readerss "0.1.1"
  :description "RSS feed extracter, it will consume a rss feed and retrieve its entries as raw text, also works with pdf"
  :url "https://www.nubank.com.br"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [adamwynne/feedparser-clj "0.5.2"]
                 [org.clojure/tools.reader "1.3.2"]
                 [commons-validator "1.5.1"]
                 [com.novemberain/pantomime "2.10.0"]
                 [remus "0.1.0-SNAPSHOT"]
                 [clj-http "3.9.1"]
                 [overtone/at-at "1.2.0"]
                 [digest "1.4.8"]]
  :jvm-opts ["-Xmx4g" "-server"]
  :main ^:skip-aot readerss.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
