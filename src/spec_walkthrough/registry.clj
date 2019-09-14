(ns spec-walkthrough.registry
  (:require
    [clojure.spec.alpha :as s])
  (:import
    (java.util Date)))

(s/def ::date inst?)

(s/def ::suit #{:club :diamond :heart :spade})

(s/valid? ::date (Date.))
;;=> true
(s/conform ::suit :club)
;;=> :club

(doc ::date)
;-------------------------
;:user/date
;Spec
;inst?

(doc ::suit)
;-------------------------
;:user/suit
;Spec
;#{:spade :heart :diamond :club}
