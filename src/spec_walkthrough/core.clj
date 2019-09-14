(ns spec-walkthrough.core
  (:require
    [clojure.spec.alpha :as s]
    [clojure.spec.alpha :as spec])
  (:import
    (java.util Date)))

; overview and rationale

(spec/keys :req [::x ::y (or ::secret (and ::user ::pwd))] :opt [::z])

(spec/keys :req-un [:my.ns/a :my.ns/b])

(s/def ::even? (s/and integer? even?))
(s/def ::odd? (s/and integer? odd?))
(s/def ::a integer?)
(s/def ::b integer?)
(s/def ::c integer?)
(def s (s/cat :forty-two #{42}
              :odds (s/+ ::odd?)
              :m (s/keys :req-un [::a ::b ::c])
              :oes (s/* (s/cat :o ::odd? :e ::even?))
              :ex (s/alt :odd ::odd? :even ::even?)))

(s/conform s [42 11 13 15 {:a 1 :b 2 :c 3} 1 2 3 42 43 44 11])


(s/conform even? 1000)
;;=> 1000

(s/valid? even? 10)
;;=> true

(s/valid? nil? nil)  ;; true
(s/valid? string? "abc")  ;; true

(s/valid? #(> % 5) 10) ;; true
(s/valid? #(> % 5) 0) ;; false

(s/valid? inst? (Date.))  ;; true

(s/valid? #{:club :diamond :heart :spade} :club) ;; true
(s/valid? #{:club :diamond :heart :spade} 42) ;; false

(s/valid? #{42} 42) ;; true
