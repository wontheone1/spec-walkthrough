(ns spec-walkthrough.collections
  (:require [clojure.spec.alpha :as s]))

(s/conform (s/coll-of keyword?) [:a :b :c])
;;=> [:a :b :c]
(s/conform (s/coll-of number?) #{5 10 2})
;;=> #{2 5 10}

(s/def ::vnum3 (s/coll-of number? :kind vector? :count 3 :distinct true :into #{}))
(s/conform ::vnum3 [1 2 3])
;;=> #{1 2 3}
(s/explain ::vnum3 #{1 2 3})   ;; not a vector
;; #{1 3 2} - failed: vector? spec: :user/vnum3
(s/explain ::vnum3 [1 1 1])    ;; not distinct
;; [1 1 1] - failed: distinct? spec: :user/vnum3
(s/explain ::vnum3 [1 2 :a])   ;; not a number
;; :a - failed: number? in: [2] spec: :user/vnum3

; Both coll-of and map-of will conform all of their elements, which may make them unsuitable for large collections.
; In that case, consider every or for maps every-kv.

;;
;; While coll-of is good for homogenous collections of any size, another case is a fixed-size positional collection with fields of known type at different positions. For that we have tuple.
;;

(s/def ::point (s/tuple double? double? double?))
(s/conform ::point [1.5 2.5 -0.5])
;=> [1.5 2.5 -0.5]

(s/def ::scores (s/map-of string? int?))
(s/conform ::scores {"Sally" 1000, "Joe" 500})
(s/def ::scores (s/map-of string? int? :conform-keys true))
(s/conform ::scores {"Sally" 1000, "Joe" 500})
;=> {"Sally" 1000, "Joe" 500}
(s/def ::scores (s/map-of string? int? :count 3))
;{"Sally" 1000, "Joe" 500} - failed: (= 3 (count %)) spec: :spec-walkthrough.core/scores
;=> nil
