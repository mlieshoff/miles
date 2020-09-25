
package org.mili.generator.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für modelType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="modelType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="interface" type="{}interfaceType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="class" type="{}classType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="enum" type="{}enumType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="useLicense" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="useGenerated" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "modelType", propOrder = {
    "_interface",
    "clazz",
    "_enum"
})
public class ModelType {

    @XmlElement(name = "interface")
    protected List<InterfaceType> _interface;
    @XmlElement(name = "class")
    protected List<ClassType> clazz;
    @XmlElement(name = "enum")
    protected List<EnumType> _enum;
    @XmlAttribute(name = "useLicense")
    protected Boolean useLicense;
    @XmlAttribute(name = "useGenerated")
    protected Boolean useGenerated;

    /**
     * Gets the value of the interface property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the interface property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInterface().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InterfaceType }
     * 
     * 
     */
    public List<InterfaceType> getInterface() {
        if (_interface == null) {
            _interface = new ArrayList<InterfaceType>();
        }
        return this._interface;
    }

    /**
     * Gets the value of the clazz property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the clazz property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClazz().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClassType }
     * 
     * 
     */
    public List<ClassType> getClazz() {
        if (clazz == null) {
            clazz = new ArrayList<ClassType>();
        }
        return this.clazz;
    }

    /**
     * Gets the value of the enum property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the enum property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEnum().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EnumType }
     * 
     * 
     */
    public List<EnumType> getEnum() {
        if (_enum == null) {
            _enum = new ArrayList<EnumType>();
        }
        return this._enum;
    }

    /**
     * Ruft den Wert der useLicense-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUseLicense() {
        return useLicense;
    }

    /**
     * Legt den Wert der useLicense-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUseLicense(Boolean value) {
        this.useLicense = value;
    }

    /**
     * Ruft den Wert der useGenerated-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUseGenerated() {
        return useGenerated;
    }

    /**
     * Legt den Wert der useGenerated-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUseGenerated(Boolean value) {
        this.useGenerated = value;
    }

}
