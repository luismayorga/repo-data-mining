(defproject nose "0.1.0-SNAPSHOT"
  :description "App to detect the evolution of bad smells over several versions of a Java SVN repository"
  :url "https://github.com/rapsioux/nose"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [commons-io/commons-io "2.4"]
                 [org.tmatesoft.svnkit/svnkit "1.8.3-1"]]
  :main ^:skip-aot nose.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
