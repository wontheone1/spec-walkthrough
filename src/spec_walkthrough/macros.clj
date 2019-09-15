(ns spec-walkthrough.macros
  (:require
    [clojure.spec.alpha :as s]))

(s/fdef clojure.core/declare
        :args (s/cat :names (s/* simple-symbol?))
        :ret any?)

(declare 100)
;; Syntax error macroexpanding clojure.core/declare at (REPL:1:1).
;; 100 - failed: simple-symbol? at: [:names]
