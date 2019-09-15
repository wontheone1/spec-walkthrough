(ns spec-walkthrough.sequences
  (:require
    [clojure.spec.alpha :as s]))

(s/def ::ingredient (s/cat :quantity number? :unit keyword?))

(s/conform ::ingredient [2 :teaspoon])
;;=> {:quantity 2, :unit :teaspoon}

;; pass string for unit instead of keyword
(s/explain ::ingredient [11 "peaches"])
;; "peaches" - failed: keyword? in: [1] at: [:unit] spec: :user/ingredient

;; leave out the unit
(s/explain ::ingredient [2])
;; () - failed: Insufficient input at: [:unit] spec: :user/ingredient

(s/def ::seq-of-keywords (s/* keyword?))
(s/conform ::seq-of-keywords [:a :b :c])
;;=> [:a :b :c]
(s/explain ::seq-of-keywords [10 20])
;; 10 - failed: keyword? in: [0] spec: :user/seq-of-keywords

(s/def ::odds-then-maybe-even (s/cat :odds (s/+ odd?)
                                     :even (s/? even?)))
(s/conform ::odds-then-maybe-even [1 3 5 100])
;;=> {:odds [1 3 5], :even 100}
(s/conform ::odds-then-maybe-even [1])
;;=> {:odds [1]}
(s/explain ::odds-then-maybe-even [100])
;; 100 - failed: odd? in: [0] at: [:odds] spec: :user/odds-then-maybe-even

;; opts are alternating keywords and booleans
(s/def ::opts (s/* (s/cat :opt keyword? :val boolean?)))
(s/conform ::opts [:silent? false :verbose true])
;;=> [{:opt :silent?, :val false} {:opt :verbose, :val true}]

(s/def ::config (s/*
                  (s/cat :prop string?
                         :val  (s/alt :s string? :b boolean?))))
(s/conform ::config ["-server" "foo" "-verbose" true "-user" "joe"])

(s/describe ::seq-of-keywords)
;;=> (* keyword?)
(s/describe ::odds-then-maybe-even)
;;=> (cat :odds (+ odd?) :even (? even?))
(s/describe ::opts)
;;=> (* (cat :opt keyword? :val boolean?))

(s/def ::even-strings (s/& (s/* string?) #(even? (count %))))
(s/valid? ::even-strings ["a"])  ;; false
(s/valid? ::even-strings ["a" "b"])  ;; true
(s/valid? ::even-strings ["a" "b" "c"])  ;; false
(s/valid? ::even-strings ["a" "b" "c" "d"])  ;; true

; use an explicit call to s/spec to start a new nested regex context

(s/def ::nested
  (s/cat :names-kw #{:names}
         :names (s/spec (s/* string?))
         :nums-kw #{:nums}
         :nums (s/spec (s/* number?))))
(s/conform ::nested [:names ["a" "b"] :nums [1 2 3]])
;;=> {:names-kw :names, :names ["a" "b"], :nums-kw :nums, :nums [1 2 3]}

(s/def ::unnested
  (s/cat :names-kw #{:names}
         :names (s/* string?)
         :nums-kw #{:nums}
         :nums (s/* number?)))
(s/conform ::unnested [:names "a" "b" :nums 1 2 3])
;;=> {:names-kw :names, :names ["a" "b"], :nums-kw :nums, :nums [1 2 3]}
