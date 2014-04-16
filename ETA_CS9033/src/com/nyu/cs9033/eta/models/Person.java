package com.nyu.cs9033.eta.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable {
	
	private String name;
	
	public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {
		public Person createFromParcel(Parcel p) {
			Person people = new Person();
			people.name = p.readString();
			return people;
//			return new Person(p);
		}

		public Person[] newArray(int size) {
			return new Person[size];
		}
	};
	
	/**
	 * Create a Person model object from a Parcel
	 * 
	 * @param p The Parcel used to populate the
	 * Model fields.
	 */
	public Person(Parcel p) {
		
		name = p.readString();
	}
	
	/**
	 * Create a Person model object from arguments
	 * 
	 * @param args Add arbitrary number of arguments to
	 * instantiate trip class.
	 */
	public Person(String args) {
		
		// TODO - fill in here, please note you must have more arguments here
	}

	public Person() {
		name = null;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		
		arg0.writeString(name);
	}
	
	/**
	 * Feel free to add additional functions as necessary below.
	 */

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Do not implement
	 */
	@Override
	public int describeContents() {
		// Do not implement!
		return 0;
	}
}
