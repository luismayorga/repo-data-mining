(ns nose.core
  (:gen-class)
  (:require [clojure.java.shell :as shell]
            [clojure.string])
  (:import (java.lang System)
           (java.io File)
           (org.apache.commons.io FileUtils)
           (org.tmatesoft.svn.core.wc2 SvnOperationFactory 
                                       SvnTarget
                                       SvnUpdate)
           (org.tmatesoft.svn.core.wc SVNRevision)
           (org.tmatesoft.svn.core.io SVNRepositoryFactory)
           (org.tmatesoft.svn.core SVNURL)
           (org.tmatesoft.svn.core.internal.io.fs FSRepositoryFactory)))


(def lang "java")


(defn print-progress-bar 
  [current-revision target-revision]
  (let [percent (* (/ current-revision target-revision) 100)
        bar (StringBuilder. "[")] 
    (doseq [i (range 50)]
      (cond (< i (int (/ percent 2))) (.append bar "=")
            (= i (int (/ percent 2))) (.append bar ">")
            :else (.append bar " ")))
    (.append bar (str "] " percent "%     "))
    (print "\r" "rev: " (str current-revision "/" target-revision " ") (.toString bar))
    (flush)))


(defn name-xml
  [project rev]
  (str project "_" (System/currentTimeMillis) "_" rev ".xml"))


(defn compose-xml-path
  [folder file]
  (if
    (= (#(nth % (dec (count %))) folder) \/)
    (str folder file)
    (str folder "/" file)))


(defn run-infusion
  "paths given must be absolute"
  [infusion-path working-copy-path report-file-path]
  (shell/sh infusion-path "-lang" lang "-path" working-copy-path "-hudsonreport" report-file-path))


(defn -main 
  ([infusion-path repo-path xml-output-dir] 
   (-main infusion-path repo-path xml-output-dir nil nil))
  ([infusion-path repo-path xml-output-dir rev-start] 
   (-main infusion-path repo-path xml-output-dir rev-start nil))
  ([infusion-path repo-path xml-output-dir rev-start rev-end] 
   (let [op-factory (new SvnOperationFactory)
         module-name    (#(nth % (dec (count %))) (clojure.string/split repo-path #"/"))
         sURL (SVNURL/fromFile (new File repo-path))]
     (FSRepositoryFactory/setup)
     (let [head-revision (if rev-end (read-string rev-end) (.getLatestRevision (SVNRepositoryFactory/create sURL)))]
       (loop [i (if rev-start (read-string rev-start) 1)]
         (let [revision (SVNRevision/create i)
               checkout (.createCheckout op-factory)
               source (SvnTarget/fromURL sURL revision)
               tempdir (doto 
                         (File/createTempFile "nose" (str (System/currentTimeMillis))) 
                         (.delete) 
                         (.mkdir))
               target (SvnTarget/fromFile tempdir)]
           (.setSource checkout source)
           (.setSingleTarget checkout target) 
           (.run checkout)
           (run-infusion infusion-path 
                         (.getAbsolutePath tempdir) 
                         (compose-xml-path xml-output-dir 
                                           (name-xml module-name (str i))))
           (FileUtils/deleteQuietly tempdir)
           (print-progress-bar i head-revision)
           (when
             (< i head-revision)
             (recur(+ i 1))))))
     (.dispose op-factory))))
