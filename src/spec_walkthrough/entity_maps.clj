(ns spec-walkthrough.entity-maps
  (:require
    [clojure.spec.alpha :as s]))

(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")
(s/def ::email-type (s/and string? #(re-matches email-regex %)))

(s/def ::acctid int?)
(s/def ::first-name string?)
(s/def ::last-name string?)
(s/def ::email ::email-type)
; (s/def ::email (s/and string? #(re-matches email-regex %)))

(s/def ::person (s/keys :req [::first-name ::last-name ::email]
                        :opt [::phone]))

(s/valid? ::person
          {::first-name "Bugs"
           ::last-name "Bunny"
           ::email "bugs@example.com"})
;;=> true

;; Fails required key check
(s/explain ::person
           {::first-name "Bugs"})
;; #:my.domain{:first-name "Bugs"} - failed: (contains? % :my.domain/last-name)
;;   spec: :my.domain/person
;; #:my.domain{:first-name "Bugs"} - failed: (contains? % :my.domain/email)
;;   spec: :my.domain/person

;; Fails attribute conformance
(s/explain ::person
           {::first-name "Bugs"
            ::last-name "Bunny"
            ::email "n/a"})
;; "n/a" - failed: (re-matches email-regex %) in: [:my.domain/email]
;;   at: [:my.domain/email] spec: :my.domain/email-type

;;
;; Map with unqualified keys
;;

(s/def :unq/person
  (s/keys :req-un [::first-name ::last-name ::email]
          :opt-un [::phone]))

(s/conform :unq/person
           {:first-name "Bugs"
            :last-name "Bunny"
            :email "bugs@example.com"})
;;=> {:first-name "Bugs", :last-name "Bunny", :email "bugs@example.com"}

(s/explain :unq/person
           {:first-name "Bugs"
            :last-name "Bunny"
            :email "n/a"})
;; "n/a" - failed: (re-matches email-regex %) in: [:email] at: [:email]
;;   spec: :my.domain/email-type

(s/explain :unq/person
           {:first-name "Bugs"})
;; {:first-name "Bugs"} - failed: (contains? % :last-name) spec: :unq/person
;; {:first-name "Bugs"} - failed: (contains? % :email) spec: :unq/person


;;
;; `keys*` for keyword args
;;

(s/def ::port number?)
(s/def ::host string?)
(s/def ::id keyword?)
(s/def ::server (s/keys* :req [::id ::host] :opt [::port]))
(s/conform ::server [::id :s1 ::host "example.com" ::port 5555])

;;
;; Declare entity in parts
;;

(s/def :animal/kind string?)
(s/def :animal/says string?)
(s/def :animal/common (s/keys :req [:animal/kind :animal/says]))
(s/def :dog/tail? boolean?)
(s/def :dog/breed string?)
(s/def :animal/dog (s/merge :animal/common
                            (s/keys :req [:dog/tail? :dog/breed])))
(s/valid? :animal/dog
          {:animal/kind "dog"
           :animal/says "woof"
           :dog/tail? true
           :dog/breed "retriever"})
;;=> true
