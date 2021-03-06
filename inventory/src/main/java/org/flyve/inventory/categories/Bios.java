/**
 *  LICENSE
 * 
 *  This file is part of Flyve MDM Inventory Library for Android.
 *
 *  Inventory Library for Android is a subproject of Flyve MDM.
 *  Flyve MDM is a mobile device management software.
 * 
 *  Flyve MDM is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 3
 *  of the License, or (at your option) any later version.
 *
 *  Flyve MDM is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  ---------------------------------------------------------------------
 *  @author    Rafael Hernandez - <rhernandez@teclib.com>
 *  @copyright Copyright Teclib. All rights reserved.
 *  @copyright Copyright FusionInventory.
 *  @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 *  @link      https://github.com/flyve-mdm/android-inventory-library
 *  @link      http://flyve.org/android-inventory-library/
 *  @link      https://flyve-mdm.com
 *  ---------------------------------------------------------------------
 */

package org.flyve.inventory.categories;

import android.content.Context;
import android.os.Build;

import org.flyve.inventory.FILog;
import org.flyve.inventory.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * This class get all the information of the Bios
 */
public class Bios extends Categories {

	/*
	 * The serialization runtime associates with each serializable class a version number,
	 * called a serialVersionUID, which is used during deserialization to verify that the sender
	 * and receiver of a serialized object have loaded classes for that object that are compatible
	 * with respect to serialization. If the receiver has loaded a class for the object that has a
	 * different serialVersionUID than that of the corresponding sender's class, then deserialization
	 * will result in an  InvalidClassException
	 *
	 *  from: https://stackoverflow.com/questions/285793/what-is-a-serialversionuid-and-why-should-i-use-it
	 */
	private static final long serialVersionUID = -559572118090134691L;
	private static final String CPUINFO = "/proc/cpuinfo";

	// <!ELEMENT BIOS (SMODEL, SMANUFACTURER, SSN, BDATE, BVERSION,
	//	BMANUFACTURER, MMANUFACTURER, MSN, MMODEL, ASSETTAG, ENCLOSURESERIAL,
	//	BIOSSERIAL, TYPE, SKUNUMBER)>

	/**
	 * This constructor trigger get all the information about Bios
	 * @param xCtx Context where this class work
	 */
	public Bios(Context xCtx) {
		super(xCtx);

		try {
			Category c = new Category("BIOS", "bios");

			// Bios Date
			c.put("BDATE", new CategoryValue(getBiosDate(), "BDATE", "biosReleaseDate"));

			// Bios Manufacturer
			c.put("BMANUFACTURER", new CategoryValue(getBiosManufacturer(), "BMANUFACTURER", "biosManufacturer"));

			// Bios version
			c.put("BVERSION", new CategoryValue(getBiosVersion(), "BVERSION", "biosVersion"));

			// Mother Board Manufacturer
			c.put("MMANUFACTURER", new CategoryValue(getMotherBoardManufacturer(), "MMANUFACTURER", "motherBoardManufacturer"));

			// Mother Board Model
			c.put("SMODEL", new CategoryValue(getMotherBoardModel(), "SMODEL", "motherBoardModel"));

			// Mother Board Serial Number
			c.put("SSN", new CategoryValue(getSystemSerialNumber(xCtx), "SSN", "motherBoardSerialNumber"));

			// Build Tag
			c.put("ASSETTAG", new CategoryValue(getBuildTag(), "ASSETTAG", "assettag"));

			// Build Tag
			c.put("MSN", new CategoryValue(getMotherBoardSerial(), "MSN", "msn"));

			this.add(c);
		} catch (Exception ex) {
			FILog.e(ex.getMessage());
		}
	}

	/**
	 * Get the Bios Date
	 * @return string with the date in simple format
	 */
	public String getBiosDate() {
		String dateInfo = Utils.getCatInfo("/sys/devices/virtual/dmi/id/bios_date");
		return dateInfo != null ? dateInfo : "N/A";
	}

	/**
	 * Get the Bios Manufacturer
	 * @return string with the manufacturer
	 */
	public String getBiosManufacturer() {
		return Build.MANUFACTURER;
	}

	/**
	 * Get the Bios Version
	 * @return string with the bootloader version
	 */
	public String getBiosVersion() {
		String dateInfo = Utils.getCatInfo("/sys/devices/virtual/dmi/id/bios_version");
		return dateInfo != null ? dateInfo : "N/A";
	}

	/**
	 * Get the Mother Board Manufacturer
	 * @return string with the manufacturer
	 */
	public String getMotherBoardManufacturer() {
		return Build.MANUFACTURER;
	}

	/**
	 * Get the Mother Board Model
	 * @return string with the model
	 */
	public String getMotherBoardModel() {
		return Build.MODEL;
	}

	/**
	 * Get the Build Tag
	 * @return string with the model
	 */
	public String getBuildTag() {
		return Build.TAGS;
	}

	/**
	 * Get the serial mother board
	 * @return string with the serial mother board
	 */
	public String getMotherBoardSerial() {
		String dateInfo = Utils.getCatInfo("/sys/devices/virtual/dmi/id/board_serial");
		return dateInfo != null ? dateInfo : "N/A";
	}

	/**
	 * Get the System serial number
	 * @return string with the serial number
	 * @param xCtx
	 */
	public String getSystemSerialNumber(Context xCtx) {
		String systemSerialNumber = "Unknown";

		if (!Build.SERIAL.equals(Build.UNKNOWN)) {
			// Mother Board Serial Number
			// Since in 2.3.3 a.k.a gingerbread
			systemSerialNumber = Build.SERIAL;
		} else {
			//Try to get the serial by reading /proc/cpuinfo
			String serial = "";
			try {
				serial = this.getSerialNumberFromCpuinfo();
			} catch (Exception ex) {
				FILog.e(ex.getMessage());
			}

			if (!serial.equals("") && !serial.equals("0000000000000000")) {
				systemSerialNumber = serial;
			} else {
				//Last try, use the hidden API!
				serial = getSerialFromPrivateAPI();
				if (!serial.equals("")) {
					systemSerialNumber = serial;
				}
			}
		}

		return systemSerialNumber;
	}

	/**
	 * This is a call to a private api to get Serial number
	 * @return String with a Serial Device
	 */
	private String getSerialFromPrivateAPI() {
		String serial = "";
		try {
	        Class<?> c = Class.forName("android.os.SystemProperties");
	        Method get = c.getMethod("get", String.class);
	        serial = (String) get.invoke(c, "ro.serialno");
	    } catch (Exception e) {
			FILog.e(e.getMessage());
	    }
	    return serial;
	}

	/**
	 * Get the serial by reading /proc/cpuinfo
	 * @return String
	 */
	private String getSerialNumberFromCpuinfo() throws IOException {
		String serial = "";
		File f = new File(CPUINFO);
		FileReader fr = null;
		try {
			fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr, 8 * 1024);
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("Serial")) {
					FILog.d(line);
					String[] results = line.split(":");
					serial = results[1].trim();
				}
			}
			br.close();
			fr.close();
		} catch (IOException e) {
			FILog.e(e.getMessage());
		} finally {
			if(fr != null) {
				fr.close();
			}
		}

		return serial.trim();
	}
}
