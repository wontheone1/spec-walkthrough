(ns spec-walkthrough.core
  (:require
    [clojure.spec.alpha :as s]
    [clojure.spec.alpha :as spec]))

(s/conform even? 1000)
;;=> 1000

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
