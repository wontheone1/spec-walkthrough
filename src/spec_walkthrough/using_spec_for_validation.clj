(ns spec-walkthrough.using-spec-for-validation
  (:require
    [clojure.spec.alpha :as s]))

(s/def ::person (s/keys :req [::first-name ::last-name ::email]
                        :opt [::phone]))

(defn person-name
  [person]
  {:pre [(s/valid? ::person person)]
   :post [(s/valid? string? %)]}
  (str (::first-name person) " " (::last-name person)))

(person-name 42)
;;=> java.lang.AssertionError: Assert failed: (s/valid? :my.domain/person person)

(person-name {::first-name "Bugs" ::last-name "Bunny" ::email "bugs@example.com"})
;; Bugs Bunny

(defn person-name
  [person]
  (let [p (s/assert ::person person)]
    (str (::first-name p) " " (::last-name p))))

(s/check-asserts true)
(person-name 100)

(s/def ::config (s/*
                  (s/cat :prop string?
                         :val  (s/alt :s string? :b boolean?))))

(defn- set-config [prop val]
  ;; dummy fn
  (println "set" prop val))

(defn configure [input]
  (let [parsed (s/conform ::config input)]
    (if (= parsed ::s/invalid)
      (throw (ex-info "Invalid input" (s/explain-data ::config input)))
      (for [{prop :prop [_ val] :val} parsed]
        (set-config (subs prop 1) val)))))

(configure ["-server" "foo" "-verbose" true "-user" "joe"])
