;   Copyright (c) Rich Hickey. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns cljd.tool
  (:require [clojure.tools.deps.alpha :as deps]
            [clojure.tools.deps.alpha.util.session :as session]
            [clojure.tools.cli.api :as api])
  (:import (java.net URL)))

(defn -main [& args]
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
        class-loader (->> basis
                       :basis
                       :classpath-roots
                       (into [] (map #(-> %
                                        java.io.File.
                                        .toURI
                                        .toURL)))
                       (into-array java.net.URL)
                       (java.net.URLClassLoader/newInstance))]
    (.setContextClassLoader (Thread/currentThread) (clojure.lang.DynamicClassLoader. class-loader))
    (in-ns 'user)
    (apply (ns-resolve (doto 'cljd.build require) '-main) '["watch"])))

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
        class-loader (->> basis
                       :basis
                       :classpath-roots
                       (into [] (map #(-> %
                                        java.io.File.
                                        .toURI
                                        .toURL)))
                       (into-array java.net.URL)
                       (java.net.URLClassLoader/newInstance))]
    (.setContextClassLoader (Thread/currentThread) (clojure.lang.DynamicClassLoader. class-loader))
    (in-ns 'user)
    (apply (ns-resolve (doto 'cljd.build require) '-main) '[watch]))



  )
