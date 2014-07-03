(defproject nose "0.1.0-SNAPSHOT"
  :description "App to detect bad smells over several versions of a Java SVN repository"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :main ^:skip-aot nose.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
