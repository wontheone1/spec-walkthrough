(ns spec-walkthrough.multi-spec
  (:require [clojure.spec.alpha :as s]))

;; register event attributes

(s/def :event/type keyword?)
(s/def :event/timestamp int?)
(s/def :search/url string?)
(s/def :error/message string?)
(s/def :error/code int?)

;; multimethod that defines a dispatch function for choosing the selector

(defmulti event-type :event/type)
(defmethod event-type :event/search [_]
  (s/keys :req [:event/type :event/timestamp :search/url]))
(defmethod event-type :event/error [_]
  (s/keys :req [:event/type :event/timestamp :error/message :error/code]))

;; declare our multi-spec and try it out.

(s/def :event/event (s/multi-spec event-type :event/type))

(s/valid? :event/event
          {:event/type :event/search
           :event/timestamp 1463970123000
           :search/url "https://clojure.org"})
;=> true
(s/valid? :event/event
          {:event/type :event/error
           :event/timestamp 1463970123000
           :error/message "Invalid host"
           :error/code 500})
;=> true
(s/explain :event/event
           {:event/type :event/restart})
;; #:event{:type :event/restart} - failed: no method at: [:event/restart]
;;   spec: :event/event
(s/explain :event/event
           {:event/type :event/search
            :search/url 200})
;; 200 - failed: string? in: [:search/url]
;;   at: [:event/search :search/url] spec: :search/url
;; {:event/type :event/search, :search/url 200} - failed: (contains? % :event/timestamp)
;;   at: [:event/search] spec: :event/event
