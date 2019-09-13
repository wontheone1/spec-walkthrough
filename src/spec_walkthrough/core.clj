(ns spec-walkthrough.core
  (:require
    [clojure.spec.alpha :as s]))

(s/conform even? 1000)
;;=> 1000

