;   Copyright (c) Rich Hickey. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns cljd.tool
  (:require [clojure.tools.deps.alpha :as deps]
            [clojure.tools.deps.alpha.util.session :as session]))

(defn -main [& args]
  (let [{:keys [root-edn user-edn project-edn]} (deps/find-edn-maps)
        master-edn (deps/merge-edns [root-edn user-edn project-edn])
        combined-aliases (deps/combine-aliases master-edn [:cljd])
        basis (session/with-session
                (deps/calc-basis master-edn {:resolve-args (merge combined-aliases {:trace true})
                                             :classpath-args combined-aliases}))]

    (println (:classpath-roots basis))))

(comment

  (let [{:keys [root-edn user-edn project-edn]} (deps/find-edn-maps)
        master-edn (deps/merge-edns [root-edn user-edn project-edn])
        combined-aliases (deps/combine-aliases master-edn [:cljd])
        basis (session/with-session
                (deps/calc-basis master-edn {:resolve-args (merge combined-aliases {:trace true})
                                             :classpath-args combined-aliases}))]

    (:classpath-roots basis))


  )
