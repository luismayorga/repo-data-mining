(ns nose.core
  (:gen-class)
  (:require [clojure.java.shell :as shell]
            [clojure.string])
  (:import (java.lang System)
           (java.io File)
           (org.tmatesoft.svn.core.wc2 SvnOperationFactory 
                                       SvnTarget)
           (org.tmatesoft.svn.core.wc SVNRevision)
           (org.tmatesoft.svn.core SVNURL)
           (org.tmatesoft.svn.core.internal.io.fs FSRepositoryFactory)))

(def lang "java")


(defn name-xml
  [project rev]
  (str project "_" (str (System/currentTimeMillis)) "_" rev ".xml"))


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
        sURL (SVNURL/fromFile (new File repo-path))
        source (SvnTarget/fromURL sURL revision)
        tempdir (doto
                  (File/createTempFile "nose" (str (System/currentTimeMillis)))
                  .mkdir)
        target (SvnTarget/fromFile tempdir)]
    (do
      (.setSource checkout source)
      (.setSingleTarget checkout target) 
      (FSRepositoryFactory/setup)
      (prn (.getURL source))
      (prn (.getURL target))
      (.run checkout)
      (run-infusion infusion-path repo-path (compose-xml-path
                                              xml-output-dir
                                              (name-xml module-name "0")))
      (loop [i 1]
        (let [rev (SVNRevision/create i)
              update (.createUpdate op-factory)
              target (SvnTarget/fromFile tempdir i)
              head-revision (.getNumber (SVNRevision/HEAD))]
          (do
            (.setSingleTarget update target)
            (.run update)
            (run-infusion infusion-path repo-path (compose-xml-path
                                                    xml-output-dir
                                                    (name-xml module-name (str i))))
            (if
              (< i head-revision)
              (recur(+ i 1))))))
      (.dispose op-factory))))

