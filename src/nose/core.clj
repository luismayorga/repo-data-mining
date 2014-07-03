(ns nose.core
  (:gen-class)
  (:require [clojure.java.shell :as shell])
  (:import (org.tmatesoft.svn.core.wc2 SvnOperationFactory)))

(def lang "java")


(defn name-xml
  [project rev]
  (str project "_" rev ".xml"))


(defn compose-xml-path
  "takes a POSIX folder path, without the trailing '/' character and a file name"
  [folder file]
  (str folder "/" file))


(defn run-infusion
  [infusion-path repo-copy-path xml-output]
  (shell/sh infusion-path "-lang" lang "-path" repo-copy-path "-hudsonreport" xml-output))


(defn -main
  [infusion-path repo-path xml-output-dir]
  (let [op-factory (new SvnOperationFactory)
        checkout (.createCheckout op-factory)]

    ;(run-infusion infusion-path repo-path (compose-xml-path xml-output-dir (name-xml "results" "1")))
    ))

