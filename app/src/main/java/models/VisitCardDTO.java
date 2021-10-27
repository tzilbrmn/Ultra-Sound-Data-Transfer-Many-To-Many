package models;

import Utils.utils;

/**
 * This class encapsulates the visit card data in an object.
 */
public class VisitCardDTO {
    private int id;
    private String encounterDate;
    private String encounterTime;


    private VisitCardDTO(Builder builder){
        this.id = builder.id;
        this.encounterDate = builder.encounterDate;
        this.encounterTime = builder.encounterTime;
    }

    /**
     * The Builder class for the <i>Builder</i> design pattern.
     */
    public static class Builder{
        private int id; //The id of the other user- what was accepted during transfer.
        private String encounterDate;
        private String encounterTime;

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
        public Builder setEncounterTime(String encounterTime) {
            this.encounterTime = encounterTime;
            return this;
        }

        /**
         *encounter date field setter.
         * @param id The encounter date value to set.
         * @return Builder object as per the recipe of the <i>Builder</i> design pattern.
         */
        public Builder setEncounterDate(String encounterDate) {
            this.encounterDate = encounterDate;
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
        return this.encounterDate;
    }

    /**
     *
     * @return encounter date of this object
     */
    public String getEncounterTime(){
        return this.encounterTime;
    }

    //=======================================================================================

    /**
     *
     * @return A string representation of this object.
     */
    @Override
    public String toString() {
        return String.format("%d;%s;%s",this.id,this.encounterDate, this.encounterTime);
    }

    /**
     * converts the string representation to a VisitCardDTO object.
     * @param enc The string representation
     * @return The VisitCardDTO object extracted from the string.
     */
    public static VisitCardDTO stringToVisitCard(String enc){
        String[] info = enc.split(";");
        //fill empty fields in the end with empty strings
        info = utils.fillArray(info,3);
        return new Builder().
                            setId(Integer.parseInt(info[0])).
                            setEncounterDate(info[1]).
                            setEncounterTime(info[2]).
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
        info = utils.fillArray(info,1);
        return new Builder().
                setId(Integer.parseInt(info[0])).
                build();
    }
}
