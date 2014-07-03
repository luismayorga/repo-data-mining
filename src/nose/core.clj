(ns nose.core
  (:gen-class)
  (:require [clojure.java.shell :as shell]
            [clojure.string])
  (:import (java.lang System)
           (java.io File)
           (org.tmatesoft.svn.core.wc2 SvnOperationFactory 
                                       SvnTarget)
           (org.tmatesoft.svn.core.wc SVNRevision)))

(def lang "java")


(defn name-xml
  [project rev]
  (str project "_" rev ".xml"))


(defn compose-xml-path
  "takes a POSIX folder path, without the trailing '/' character and a file name"
  [folder file]
  (str folder "/" file))


(defn run-infusion
  "paths given to inFusionC must be absolute"
  [infusion-path repo-copy-path xml-output]
  (shell/sh infusion-path "-lang" lang "-path" repo-copy-path "-hudsonreport" xml-output))


(defn -main
  [infusion-path repo-path xml-output-dir]
  (let [op-factory (new SvnOperationFactory)
        module-name    (apply 
                         #(nth % (dec (count %)))
                         (vector (clojure.string/split repo-path #"/"))) 

        checkout (.createCheckout op-factory)
        revision (SVNRevision/create 0)
        source (SvnTarget/fromFile
                 (new File repo-path) revision)
        target (SvnTarget/fromFile 
                 (doto 
                   (File/createTempFile "nose" (System/currentTimeMillis))
                   .mkdir))]
    (do
      (.setSource checkout source)
      (.setSingleTarget target) 
      (.run checkout)
      (run-infusion infusion-path repo-path (compose-xml-path
                                              xml-output-dir
                                              (name-xml 
                                                module-name
                                                "0")))
      (loop [i 1]
        ;TODO
        ;change revision
        ;update
        ;run infusion
        )
      (.dispose op-factory))))

