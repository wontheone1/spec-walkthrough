(ns spec-walkthrough.composing-predicates
  (:require [clojure.spec.alpha :as s]))

(s/def ::big-even (s/and int? even? #(> % 1000)))
(s/valid? ::big-even :foo) ;; false
(s/valid? ::big-even 10) ;; false
(s/valid? ::big-even 100000) ;; true

(s/def ::name-or-id (s/or :name string?
                          :id   int?))
(s/valid? ::name-or-id "abc") ;; true
(s/valid? ::name-or-id 100) ;; true
(s/valid? ::name-or-id :foo) ;; false

(s/conform ::name-or-id "abc")
;;=> [:name "abc"]
(s/conform ::name-or-id 100)
;;=> [:id 100]

(s/valid? string? nil)
;;=> false
(s/valid? (s/nilable string?) nil)
;;=> true
