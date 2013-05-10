(defproject  org.clojars.mediocregopher/clj-mtgox "0.3"
  :description "MtGox (unofficial) api library"
  :url "http://github.com/mediocregopher/clj-mtgox"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[clj-http "0.7.2"]
                 [org.clojure/data.codec "0.1.0"]
                 [cheshire "5.1.1"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.5.1"]] }})
