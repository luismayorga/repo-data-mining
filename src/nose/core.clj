(ns nose.core
  (:gen-class)
  (:require [nose.analyse.core]
            [nose.transform.core]
            [clojure.tools.cli :refer [parse-opts]]))


(def cli-analysis-options
  [["-f" "--first-revision REV" "First revision to analyse"
    :validate [number? "Must be a number"]]
   ["-l" "--last-revision REV" "Last revision to analyse"
    :validate [number? "Must be a number"]]])


(defn run-analysis [args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-analysis-options)]
    (nose.analyse.core/main (nth arguments 0)
                            (nth arguments 1)
                            (nth arguments 2)
                            (:start options)
                            (:end options))))

(defn run-transformation [args]
  (when
    (> (count args) 1)
    (System/exit 1))
  (nose.transform.core/run (first args)))

(defn -main [& args]
  (case (first args) 
    "analyse" (run-analysis (rest args))
    "transform" (run-transformation (rest args))
    (System/exit 1)))

 
