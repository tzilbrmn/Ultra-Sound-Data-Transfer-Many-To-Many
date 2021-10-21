package models;

import Utils.utils;

/**
 * This class encapsulates the visit card data in an object.
 */
public class VisitCardDTO {
    private int id;
    private String owner;

    private VisitCardDTO(Builder builder){
        this.id = builder.id;
        this.owner = builder.owner;
    }

    /**
     * The Builder class for the <i>Builder</i> design pattern.
     */
    public static class Builder{
        private int id;
        private String owner;

        /**
         * sets the ID of this object.
         * @param id The ID value to set
         * @return Builder object as per the recipe of the <i>Builder</i> design pattern.
         */
        public Builder setId(int id) {
            this.id = id;
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
     * @return The owner of this object.
     */
    public String getOwner() {
        return owner;
    }

    //=======================================================================================

    /**
     *
     * @return A string representation of this object.
     */
    @Override
    public String toString() {
        return String.format("%d;%s",this.id,this.owner);
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
    public static VisitCardDTO stringToVisitCard(String enc){
        String[] info = enc.split(";");
        //fill empty fields in the end with empty strings
        info = utils.fillArray(info,14);
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
