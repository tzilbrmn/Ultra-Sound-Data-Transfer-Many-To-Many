package models;

import Utils.utils;

/**
 * This class encapsulates the visit card data in an object.
 */
public class VisitCardDTO {
    private int id;
    private String encounter_date;
    private String encounter_time;


    private VisitCardDTO(Builder builder){
        this.id = builder.id;
        this.encounter_date = builder.encounter_date;
        this.encounter_time = builder.encounter_time;
    }

    /**
     * The Builder class for the <i>Builder</i> design pattern.
     */
    public static class Builder{
        private int id;
        private String encounter_date;
        private String encounter_time;

        /**
         * sets the ID of this object.
         * @param The ID value to set
         * @return Builder object as per the recipe of the <i>Builder</i> design pattern.
         */
        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        /**
         * encounter time field setter.
         * @param The encounter time value to set.
         * @return Builder object as per the recipe of the <i>Builder</i> design pattern.
         */
        public Builder setEncounterTime(String encounter_time) {
            this.encounter_time = encounter_time;
            return this;
        }

        /**
         *encounter date field setter.
         * @param id The encounter date value to set.
         * @return Builder object as per the recipe of the <i>Builder</i> design pattern.
         */
        public Builder setEncounterDate(String encounter_date) {
            this.encounter_date = encounter_date;
            return this;
        }


        /**
         * sets the owner of this object.
         * @param owner The owner value to set.
         * @return Builder object as per the recipe of the <i>Builder</i> design pattern.
         * @throws IllegalArgumentException thrown if the argument is an empty string.
         */
        public Builder setOwner(String owner) throws IllegalArgumentException {
            if(owner.isEmpty()) throw new IllegalArgumentException("Visit Card must have owner");
            this.owner = owner;
            return this;
        }

        //===========================================

        /**
         * builds the VisitCardDTO object.
         * @return the newly built VisitCardDTO object.
         */
        public VisitCardDTO build() {
            return new VisitCardDTO(this);
        }
    }
    //=======================================================================================

    /**
     *
     * @return The ID of this object.
     */
    public int getId() {
        return id;
    }


    /**
     *
     * @return encounter date of this object
     */
    public String getEncounterDate(){
        return this.encounter_date;
    }

    /**
     *
     * @return encounter date of this object
     */
    public String getEncounterTime(){
        return this.encounter_time;
    }

    //=======================================================================================

    /**
     *
     * @return A string representation of this object.
     */
    @Override
    public String toString() {
        return String.format("%d;%s;%s",this.id,this.encounter_date, this.encounter_time);
    }

    /**
     * encodes this object to a string to prepare it for transmission over sound
     * @return A string encoding ready to be transmitted over sound.
     */
    public String prepareForCompression(){
        return String.format("%s;%s;",this.id);
    }

    /**
     * converts the string representation to a VisitCardDTO object.
     * @param enc The string representation
     * @return The VisitCardDTO object extracted from the string.
     */
    //????????????????????????????????????????????????????????????????????
    public static VisitCardDTO stringToVisitCard(String enc){
        String[] info = enc.split(";");
        //fill empty fields in the end with empty strings
        info = utils.fillArray(info,14);//??????????????????????????????????
        return new Builder().
                            setId(Integer.parseInt(info[0])).
                            setOwner(info[1]).
                            build();
    }

    /**
     * converts the string encoding received via sound to a VisitCardDTO object
     * @param enc The string encoding received via sound
     * @return The VisitCardDTO object extracted from the received string.
     */
    public static VisitCardDTO receiveVisitCard(String enc) throws IndexOutOfBoundsException,IllegalArgumentException{
        String[] info = enc.split(";");
        //fill empty fields in the end with empty strings
        info = utils.fillArray(info,12);
        return new Builder().
                setId(Integer.parseInt(info[0])).
                build();
    }
}
