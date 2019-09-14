(ns spec-walkthrough.predicates
  (:require
    [clojure.spec.alpha :as s])
  (:import
    (java.util Date)))

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
