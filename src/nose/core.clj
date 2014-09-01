(ns nose.core
  (:gen-class)
  (:require [nose.analyse.core]
            [nose.transform.core]
            [nose.transform.db :as db]
            [clojure.tools.cli :refer [parse-opts]]))


(def cli-analysis-options
  [["-f" "--first-revision REV" "First revision to analyse"
    :default 0
    :parse-fn #(Long/parseLong %)
    :validate [number? "Must be a number"]]
   ["-l" "--last-revision REV" "Last revision to analyse"
    :default -1
    :default-desc "last"
    :parse-fn #(Long/parseLong %)
    :validate [number? "Must be a number"]]
   ["-s" "--step INCREMENT" "Increment between consecutive versions analysed"
    :default 1
    :parse-fn #(Long/parseLong %)
    :validate [number? "Must be a number"]] ])

(defn run-db [args]
  (db/create-db))

(defn run-analysis [args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-analysis-options)]
    (prn options)
    (nose.analyse.core/main (nth arguments 0)
                            (nth arguments 1)
                            (nth arguments 2)
                            (:first-revision options)
                            (:last-revision options)
                            (:step options))))

(defn run-transformation [args]
  (when
    (not= (count args) 1)
    (System/exit 1))
  (nose.transform.core/run (first args)))

(defn -main [& args]
  (case (first args) 
    "db" (run-db (rest args))
    "analyse" (run-analysis (rest args))
    "transform" (run-transformation (rest args))
    (System/exit 1)))

 
