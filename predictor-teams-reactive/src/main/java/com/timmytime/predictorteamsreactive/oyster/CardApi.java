package com.timmytime.predictorteamsreactive.oyster;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class CardApi {

    @Getter
    private final Card card = new Card(UUID.randomUUID(), 0.0);

    public boolean tapIn(UUID cardId, Station station){ //TODO station

        return true;
    }

    public boolean tapOut(UUID cardId, Station station){ //TODO station

        return true;
    }

    public boolean addToBalance(UUID cardId, double amount){
        card.updateBalance(amount);
        return true;
    }

    void calcFare(){

    }


    class Card{

        @Getter
        private double balance;
        private UUID id;
        public Card(UUID id, double balance){
            this.id = id;
            this.balance = balance;
        }

        public void updateBalance(double updated){
            //validation update..
            this.balance += updated;
        }
    }

}
