(ns spec-walkthrough.a-game-of-cards
  (:require
    [clojure.spec.alpha :as s]))

(def suit? #{:club :diamond :heart :spade})
(def rank? (into #{:jack :queen :king :ace} (range 2 11)))
(def deck (for [suit suit? rank rank?] [rank suit]))

(s/def ::card (s/tuple rank? suit?))
(s/def ::hand (s/* ::card))

(s/def ::name string?)
(s/def ::score int?)
(s/def ::player (s/keys :req [::name ::score ::hand]))

(s/def ::players (s/* ::player))
(s/def ::deck (s/* ::card))
(s/def ::game (s/keys :req [::players ::deck]))

(def kenny
  {::name "Kenny Rogers"
   ::score 100
   ::hand []})
(s/valid? ::player kenny)
;;=> true

(s/explain ::game
           {::deck deck
            ::players [{::name "Kenny Rogers"
                        ::score 100
                        ::hand [[2 :banana]]}]})
;; :banana - failed: suit? in: [:user/players 0 :user/hand 0 1]
;;   at: [:user/players :user/hand 1] spec: :user/card

(defn total-cards [{:keys [::deck ::players] :as game}]
  (apply + (count deck)
         (map #(-> % ::hand count) players)))

(defn deal [game] .... )

(s/fdef deal
        :args (s/cat :game ::game)
        :ret ::game
        :fn #(= (total-cards (-> % :args :game))
                (total-cards (-> % :ret))))
