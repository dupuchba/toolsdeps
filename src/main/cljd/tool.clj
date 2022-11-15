(ns cljd.tool
  (:require [clojure.tools.deps.alpha :as deps]
            [clojure.tools.deps.alpha.util.session :as session]
            [clojure.tools.cli.api :as api])
  (:import (java.net URL)))

(defn init-project []
  (let [{:keys [root-edn user-edn project-edn]} (deps/find-edn-maps)
        master-edn (deps/merge-edns [root-edn user-edn project-edn])
        basis (api/basis master-edn)
        _ (deps/prep-libs! (:libs (:basis basis)) {:action :prep
                                                   :log :info
                                                   :current false} (:basis basis))
        class-loader (->> basis
                       :basis
                       :classpath-roots
                       (into [] (map #(-> %
                                        java.io.File.
                                        .toURI
                                        .toURL)))
                       (into-array java.net.URL)
                       (java.net.URLClassLoader/newInstance))]
    (.setContextClassLoader (Thread/currentThread) (clojure.lang.DynamicClassLoader. class-loader))))

(defn watch [& args]
  (init-project)
  (in-ns 'user)
  (apply (ns-resolve (doto 'cljd.build require) '-main) (into ["watch"] (map name) args)))

(defn compile [& args]
  (init-project)
  (in-ns 'user)
  (apply (ns-resolve (doto 'cljd.build require) '-main) (into ["compile"] (map name) args)))

(defn flutter [& args]
  (print args)
  (init-project)
  (in-ns 'user)
  (apply (ns-resolve (doto 'cljd.build require) '-main) (into ["flutter"] (map name) args)))

(defn init [& args]
  (init-project)
  (in-ns 'user)
  (apply (ns-resolve (doto 'cljd.build require) '-main) (into ["init"] (map name) args)))

(defn clean []
  (init-project)
  (in-ns 'user)
  (apply (ns-resolve (doto 'cljd.build require) '-main) ["clean"]))

(comment

  (let [{:keys [root-edn user-edn project-edn]} (deps/find-edn-maps)
        master-edn (deps/merge-edns [root-edn user-edn project-edn])
        #_#_combined-aliases (deps/combine-aliases master-edn [:cljd])
        #_#_basis (session/with-session
                    (deps/calc-basis master-edn {:resolve-args (merge combined-aliases {:trace true})
                                                 :classpath-args combined-aliases}))
        basis (api/basis
                (assoc master-edn
                  :extra {:aliases {:cljdd {:main-opts ["-m" "cljd.build"]}}}
                  :aliases [:cljdd]))
        _ (deps/prep-libs! (:libs (:basis basis)) {:action :prep
                                                   :log :info
                                                   :current false} (:basis basis))
        class-loader (->> basis
                       :basis
                       :classpath-roots
                       (into [] (map #(-> %
                                        java.io.File.
                                        .toURI
                                        .toURL)))
                       (into-array java.net.URL)
                       (java.net.URLClassLoader/newInstance))]
    )



  )
