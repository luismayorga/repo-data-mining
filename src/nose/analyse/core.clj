(ns nose.analyse.core
  (:gen-class)
  (:require [clojure.java.shell :as shell]
            [clojure.string]
            [clj-progress.core :as prog])
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


(defn- name-xml
  [project rev]
  (str project "_" (System/currentTimeMillis) "_" rev ".xml"))


(defn- compose-xml-path
  [folder file]
  (if
    (= (#(nth % (dec (count %))) folder) \/)
    (str folder file)
    (str folder "/" file)))


(defn run-infusion
  "paths given must be absolute"
  [infusion-path working-copy-path report-file-path]
  (shell/sh infusion-path "-lang" lang "-path" working-copy-path "-hudsonreport" report-file-path))


(defn- checkout-repo
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


(defn- update-repo
  "updates repo in path to revision rev"
  [op-factory repo-path wc-path rev]
  (let [
        clientManager (SVNClientManager/newInstance)
        updateClient (.getUpdateClient clientManager)
        revision (SVNRevision/create rev)]
    (.doUpdate updateClient (new File wc-path) revision SVNDepth/INFINITY true true)))


(defn- clean-repo
  "deletes repository"
  [repo-path]
  (FileUtils/deleteQuietly (new File repo-path)))


(defn main 
  [infusion-path repo-path xml-output-dir rev-start rev-end step] 
  (prog/init (/ (- rev-end rev-start) step))
  (FSRepositoryFactory/setup)
  (let [op-factory (new SvnOperationFactory)
        module-name    (#(nth % (dec (count %))) (clojure.string/split repo-path #"/"))
        sURL (SVNURL/fromFile (new File repo-path))
        head-revision (if (= rev-end -1) (.getLatestRevision (SVNRepositoryFactory/create sURL) rev-end))
        working-copy (checkout-repo op-factory sURL rev-start)]

    (run-infusion infusion-path 
                  working-copy
                  (compose-xml-path xml-output-dir (name-xml module-name (str rev-start))))
    (prog/tick)
    (doseq [i (range rev-start rev-end step)]
      (update-repo op-factory repo-path working-copy i)
      (run-infusion infusion-path 
                    working-copy
                    (compose-xml-path xml-output-dir (name-xml module-name (str i))))
      (prog/tick)
      (prog/done)
      (clean-repo working-copy)
      (.dispose op-factory))))
