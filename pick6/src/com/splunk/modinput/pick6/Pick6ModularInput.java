package com.splunk.modinput.pick6;

import java.util.Date;
import java.util.Random;
import java.util.HashMap;
import java.util.ArrayList;

import java.util.List;

import com.splunk.modinput.Arg;
import com.splunk.modinput.Endpoint;
import com.splunk.modinput.Input;
import com.splunk.modinput.Item;
import com.splunk.modinput.ModularInput;
import com.splunk.modinput.Param;
import com.splunk.modinput.Scheme;
import com.splunk.modinput.Stream;
import com.splunk.modinput.StreamEvent;

import com.splunk.modinput.Stanza;

import com.splunk.modinput.Validation;
import com.splunk.modinput.ValidationError;
import com.splunk.modinput.Scheme.StreamingMode;

public class Pick6ModularInput extends ModularInput {

	public static void main(String[] args) {

		Pick6ModularInput instance = new Pick6ModularInput();
		instance.init(args);

	}

	@Override
	protected void doRun(Input input) throws Exception {

		if (input != null) {

			for (Stanza stanza : input.getStanzas()) {

				String name = stanza.getName();

				if (name != null) {

					List<Param> params = stanza.getParams();
					for (Param param : params) {
						String value = param.getValue();
						if (value == null) {
							continue;
						}

						if (param.getName().equals("seed")) {

							new Pick6Thread(value, name).start();

						}
					}

				}

				else {
					logger.error("Invalid stanza name : " + name);
					System.exit(2);
				}

			}
		} else {
			logger.error("Input is null");
			System.exit(2);
		}

	}

	class Pick6Thread extends Thread {

		String value;
		String stanzaName;

		Pick6Thread(String value, String stanzaName) {

			this.value = value;
			this.stanzaName = stanzaName;
		}

	    public String get_eventString(int seed) {
		HashMap<Integer,Integer> randfound = new HashMap<Integer,Integer>();
		ArrayList<Integer> lotNum = new  ArrayList<Integer>();
		Random rand = new Random(seed);
		int pick=Math.abs(rand.nextInt()%49);
		if (pick==0)
		    pick++;
		int count=0;
		while (count<6) {
		    if (randfound.get(pick)==null) {
			lotNum.add(pick);
			randfound.put(pick, pick);
			count++;
		    }
		    else {
			pick=Math.abs(rand.nextInt()%49);
			if (pick==0)
			    pick++;
		    }
		}
		String str = new Date() + " Pick 6 Numbers: ";
		for(int i:lotNum)
		    str=str+i+" ";
		return str;
	    }

		public void run() {

		    int seed = Integer.parseInt(value);
			while (!isDisabled(stanzaName)) {

			    String eventString=get_eventString(seed);

				Stream stream = new Stream();

				StreamEvent event = new StreamEvent();
				event.setData(eventString);
				event.setStanza(stanzaName);
				ArrayList<StreamEvent> list = new ArrayList<StreamEvent>();
				list.add(event);
				stream.setEvents(list);
				marshallObjectToXML(stream);
				seed++;
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {

				}
			}

		}
	}

	@Override
	protected void doValidate(Validation val) {

		try {

			if (val != null) {

				List<Item> items = val.getItems();
				for (Item item : items) {
					List<Param> params = item.getParams();

					for (Param param : params) {
						if (param.getName().equals("seed")) {
							String value = param.getValue();
							int v=Integer.parseInt(value);
							if (v<1 ||  v>49) {
								throw new Exception(
										"Seed must be greater than 0 and less than 50");
							}
						}
					}
				}
			}
			System.exit(0);
		} catch (Exception e) {
			logger.error(e.getMessage());
			ValidationError error = new ValidationError("Validation Failed : "
					+ e.getMessage());
			sendValidationError(error);
			System.exit(2);
		}

	}

	@Override
	protected Scheme getScheme() {

		Scheme scheme = new Scheme();
		scheme.setTitle("Pick 6");
		scheme.setDescription("Pick 6 Modular Input");
		scheme.setUse_external_validation(true);
		scheme.setUse_single_instance(true);
		scheme.setStreaming_mode(StreamingMode.XML);

		Endpoint endpoint = new Endpoint();

		Arg arg = new Arg();
		arg.setName("name");
		arg.setTitle("Input Name");
		arg.setDescription("Name of the input");

		endpoint.addArg(arg);

		arg = new Arg();
		arg.setName("seed");
		arg.setTitle("Seed");
		arg.setDescription("Seed for Random Number to Generate Pick 6");
		endpoint.addArg(arg);

		scheme.setEndpoint(endpoint);

		return scheme;
	}

}
