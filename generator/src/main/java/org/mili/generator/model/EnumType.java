
package org.mili.generator.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for enumType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="enumType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="const" type="{}constType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="implements" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "enumType", propOrder = {
    "_const"
})
public class EnumType {

    @XmlElement(name = "const")
    protected List<ConstType> _const;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "implements")
    protected String _implements;

    /**
     * Gets the value of the const property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the const property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConst().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConstType }
     * 
     * 
     */
    public List<ConstType> getConst() {
        if (_const == null) {
            _const = new ArrayList<ConstType>();
        }
        return this._const;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the implements property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImplements() {
        return _implements;
    }

    /**
     * Sets the value of the implements property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImplements(String value) {
        this._implements = value;
    }

}
