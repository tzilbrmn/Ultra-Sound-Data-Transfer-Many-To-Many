package models;

import Utils.utils;

/**
 * This class encapsulates the visit card data in an object.
 */
public class VisitCardDTO {
    private int id;
    private String owner;
    private String email;
    private String prefix;
    private String first_name;
    private String middle_name;
    private String last_name;
    private String position_title;
    private String company;
    private String address;
    private String telephone;
    private String fax;
    private String mobile;
    private String website;

    private VisitCardDTO(Builder builder){
        this.id = builder.id;
        this.owner = builder.owner;
        this.email = builder.email;
        this.prefix = builder.prefix;
        this.first_name = builder.first_name;
        this.middle_name = builder.middle_name;
        this.last_name = builder.last_name;
        this.position_title = builder.position_title;
        this.company = builder.company;
        this.address = builder.address;
        this.telephone = builder.telephone;
        this.fax = builder.fax;
        this.mobile = builder.mobile;
        this.website = builder.website;
    }

    /**
     * The Builder class for the <i>Builder</i> design pattern.
     */
    public static class Builder{
        private int id;
        private String owner;
        private String email;
        private String prefix;
        private String first_name;
        private String middle_name;
        private String last_name;
        private String position_title;
        private String company;
        private String address;
        private String telephone;
        private String fax;
        private String mobile;
        private String website;

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

        /**
         * sets the email of this object.
         * @param email The email value to set.
         * @return Builder object as per the recipe of the <i>Builder</i> design pattern.
         * @throws IllegalArgumentException thrown if the argument is an empty string.
         */
        public Builder setEmail(String email) throws IllegalArgumentException {
            if(email.isEmpty()) throw new IllegalArgumentException("This field is mandatory");
            this.email = email;
            return this;
        }

        /**
         * sets the prefix of this object.
         * @param prefix The prefix value to set.
         * @return Builder object as per the recipe of the <i>Builder</i> design pattern.
         */
        public Builder setPrefix(String prefix){
            this.prefix = prefix;
            return this;
        }

        /**
         * sets the first_name of this object.
         * @param first_name The first_name value to set.
         * @return Builder object as per the recipe of the <i>Builder</i> design pattern.
         * @throws IllegalArgumentException thrown if the argument is an empty string.
         */
        public Builder setFirst_name(String first_name) throws IllegalArgumentException {
            if(first_name.isEmpty()) throw new IllegalArgumentException("This field is mandatory");
            this.first_name = first_name;
            return this;
        }

        /**
         * sets the middle_name of this object.
         * @param middle_name The middle_name value to set.
         * @return Builder object as per the recipe of the <i>Builder</i> design pattern.
         */
        public Builder setMiddle_name(String middle_name){
            this.middle_name = middle_name;
            return this;
        }

        /**
         * sets the last_name of this object.
         * @param last_name The last_name value to set.
         * @return Builder object as per the recipe of the <i>Builder</i> design pattern.
         * @throws IllegalArgumentException thrown if the argument is an empty string.
         */
        public Builder setLast_name(String last_name) throws IllegalArgumentException {
            if(last_name.isEmpty()) throw new IllegalArgumentException("This field is mandatory");
            this.last_name = last_name;
            return this;
        }

        /**
         * sets the position_title of this object.
         * @param position_title The position_title value to set.
         * @return Builder object as per the recipe of the <i>Builder</i> design pattern.
         * @throws IllegalArgumentException thrown if the argument is an empty string.
         */
        public Builder setPosition_title(String position_title) throws IllegalArgumentException{
            if(position_title.isEmpty()) throw new IllegalArgumentException("This field is mandatory");
            this.position_title = position_title;
            return this;
        }

        /**
         * sets the company of this object.
         * @param company The company value to set.
         * @return Builder object as per the recipe of the <i>Builder</i> design pattern.
         * @throws IllegalArgumentException thrown if the argument is an empty string.
         */
        public Builder setCompany(String company) throws IllegalArgumentException{
            if(company.isEmpty()) throw new IllegalArgumentException("This field is mandatory");
            this.company = company;
            return this;
        }

        /**
         * sets the address of this object.
         * @param address The address value to set.
         * @return Builder object as per the recipe of the <i>Builder</i> design pattern.
         * @throws IllegalArgumentException thrown if the argument is an empty string.
         */
        public Builder setAddress(String address) throws IllegalArgumentException {
            if(address.isEmpty()) throw new IllegalArgumentException("This field is mandatory");
            this.address = address;
            return this;
        }

        /**
         * sets the telephone of this object.
         * @param telephone The telephone value to set.
         * @return Builder object as per the recipe of the <i>Builder</i> design pattern.
         * @throws IllegalArgumentException thrown if the argument is an empty string.
         */
        public Builder setTelephone(String telephone) throws IllegalArgumentException {
            if(telephone.isEmpty()) throw new IllegalArgumentException("This field is mandatory");
            this.telephone = telephone;
            return this;
        }

        /**
         * sets the fax of this object.
         * @param fax The fax value to set.
         * @return Builder object as per the recipe of the <i>Builder</i> design pattern.
         */
        public Builder setFax(String fax) {
            this.fax = fax;
            return this;
        }

        /**
         * sets the mobile of this object.
         * @param mobile The mobile value to set.
         * @return Builder object as per the recipe of the <i>Builder</i> design pattern.
         */
        public Builder setMobile(String mobile) {
            this.mobile = mobile;
            return this;
        }

        /**
         * sets the website of this object.
         * @param website The website value to set.
         * @return Builder object as per the recipe of the <i>Builder</i> design pattern.
         */
        public Builder setWebsite(String website) {
            this.website = website;
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

    /**
     *
     * @return The email of this object.
     */
    public String getEmail() { return email; }

    /**
     *
     * @return The prefix of this object.
     */
    public String getPrefix() { return prefix; }

    /**
     *
     * @return The first_name of this object.
     */
    public String getFirst_name() {
        return first_name;
    }

    /**
     *
     * @return The middle_name of this object.
     */
    public String getMiddle_name() { return middle_name; }

    /**
     *
     * @return The last_name of this object.
     */
    public String getLast_name() { return last_name; }

    /**
     *
     * @return The position_title of this object.
     */
    public String getPosition_title() { return position_title; }

    /**
     *
     * @return The company of this object.
     */
    public String getCompany() { return company; }

    /**
     *
     * @return The address of this object.
     */
    public String getAddress() { return address; }

    /**
     *
     * @return The telephone of this object.
     */
    public String getTelephone() { return telephone; }

    /**
     *
     * @return The fax of this object.
     */
    public String getFax() { return fax; }

    /**
     *
     * @return The mobile of this object.
     */
    public String getMobile() { return mobile; }

    /**
     *
     * @return The website of this object.
     */
    public String getWebsite() { return website; }

    //=======================================================================================

    /**
     * builds the full name of this visit card from the prefix, first, middle, and last name.<br/>
     * Example: <i>"Mr. Chandler M. Bing"</i> OR <i>"Mr. Chandler Bing"</i>.
     * @return The full name of this visit card.
     */
    public String getFullName(){
        String full_name = "";
        if(!this.middle_name.isEmpty())
            full_name = String.format("%s. %s %c. %s",this.prefix,this.first_name,Character.toUpperCase(this.middle_name.charAt(0)), this.last_name);
        else
            full_name = String.format("%s. %s %s",this.prefix,this.first_name, this.last_name);
        return full_name;
    }

    /**
     *
     * @return A string representation of this object.
     */
    @Override
    public String toString() {
        return String.format("%d;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s",this.id,this.owner,this.email,this.prefix,this.first_name,this.middle_name,this.last_name,this.position_title,this.company,this.address,this.telephone,this.fax,this.mobile,this.website);
    }

    /**
     * encodes this object to a string to prepare it for transmission over sound
     * @return A string encoding ready to be transmitted over sound.
     */
    public String prepareForCompression(){
        return String.format("%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s",this.email,this.prefix,this.first_name,this.middle_name,this.last_name,this.position_title,this.company,this.address,this.telephone,this.fax,this.mobile,this.website);
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
                            setEmail(info[2]).
                            setPrefix(info[3]).
                            setFirst_name(info[4]).
                            setMiddle_name(info[5]).
                            setLast_name(info[6]).
                            setPosition_title(info[7]).
                            setCompany(info[8]).
                            setAddress(info[9]).
                            setTelephone(info[10]).
                            setFax(info[11]).
                            setMobile(info[12]).
                            setWebsite(info[13]).
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
                setEmail(info[0]).
                setPrefix(info[1]).
                setFirst_name(info[2]).
                setMiddle_name(info[3]).
                setLast_name(info[4]).
                setPosition_title(info[5]).
                setCompany(info[6]).
                setAddress(info[7]).
                setTelephone(info[8]).
                setFax(info[9]).
                setMobile(info[10]).
                setWebsite(info[11]).
                build();
    }
}
