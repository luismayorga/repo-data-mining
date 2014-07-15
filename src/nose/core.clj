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
           (org.tmatesoft.svn.core.wc SVNRevision
                                      SVNClientManager)
           (org.tmatesoft.svn.core.io SVNRepositoryFactory)
           (org.tmatesoft.svn.core SVNURL
                                   SVNDepth)
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


(defn checkout-repo
  "checkouts repo in a temp dir and returns the path of the working copy"
  [op-factory sURL rev]
  (let [revision (SVNRevision/create rev)
        checkout (.createCheckout op-factory)
        source (SvnTarget/fromURL sURL revision)
        tempdir (doto (File/createTempFile "nose" (str (System/currentTimeMillis))) (.delete) (.mkdir))
        target (SvnTarget/fromFile tempdir)]
    (.setSource checkout source)
    (.setSingleTarget checkout target) 
    (.run checkout)
    (.getAbsolutePath tempdir)))


(defn update-repo
  "updates repo in path to revision rev"
  [op-factory repo-path wc-path rev]
  (let [
        clientManager (SVNClientManager/newInstance)
        updateClient (.getUpdateClient clientManager)
        revision (SVNRevision/create rev)]
    (.doUpdate updateClient (new File wc-path) revision SVNDepth/INFINITY true true)))


(defn clean-repo
  "deletes repository"
  [repo-path]
  (FileUtils/deleteQuietly (new File repo-path)))


(defn -main 
  ([infusion-path repo-path xml-output-dir] 
   (-main infusion-path repo-path xml-output-dir nil nil))
  ([infusion-path repo-path xml-output-dir rev-start] 
   (-main infusion-path repo-path xml-output-dir rev-start nil))
  ([infusion-path repo-path xml-output-dir rev-start rev-end] 
   (FSRepositoryFactory/setup)
   (let [op-factory (new SvnOperationFactory)
         module-name    (#(nth % (dec (count %))) (clojure.string/split repo-path #"/"))
         sURL (SVNURL/fromFile (new File repo-path))
         head-revision (if rev-end (read-string rev-end) (.getLatestRevision (SVNRepositoryFactory/create sURL)))
         first-revision (if rev-start (read-string rev-start) 1)
         working-copy (checkout-repo op-factory sURL first-revision)]
     (print-progress-bar 0 head-revision)
     (run-infusion infusion-path 
                   working-copy
                   (compose-xml-path xml-output-dir (name-xml module-name (str first-revision))))
     (print-progress-bar first-revision head-revision)
     (loop [i (+ first-revision 1)]
       (update-repo op-factory repo-path working-copy i)
       (run-infusion infusion-path 
                     working-copy
                     (compose-xml-path xml-output-dir (name-xml module-name (str i))))
       (print-progress-bar i head-revision)
       (when
         (< i head-revision)
         (recur(+ i 5))))
     (clean-repo working-copy)
     (.dispose op-factory))))
