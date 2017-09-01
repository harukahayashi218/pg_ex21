import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallFeeCalculation {

	List<String> phoneNumbers;
	Map<String,List<String>> surviceCodeMap;
	Map<String,List<String>> survicePhoneNumber;

	List<String> callLog;
	Map<String, List<String>> callLogMap;

	String inputFilePath = "C:\\Users\\hayashi\\Desktop\\課題#21\\record.log";
	String outputFilePath = "C:\\Users\\hayashi\\Desktop\\課題#21\\invoice.dat";
	int basicCallFeePerMin = 20;
	int familyDiscountCallFeePerMin = 10;
	int afternoonDiscountCallFeePerMin = 15;
	int twoDiscountsCallFeePerMin = 7;
	String disStartTime = "08:00";
	String disEndTime = "17:59";
	SimpleDateFormat sdFormat;
	String phoneNumberNo = "1";
	String basicFeeNo = "5";
	String callFeeNo = "7";
	String endLineNo = "9";
	String endLine = "====================";


	public static void main(String[] args) {

		CallFeeCalculation ccc = new CallFeeCalculation();
		List<String> logList = ccc.readLog();
		ccc.registerLogData(logList);
		//ccc.calculateFee();
	}

	//全行格納
	public List<String> readLog() {

		List<String> logList = new ArrayList<String>();

		try {
			File file = new File(inputFilePath);
			BufferedReader br = new BufferedReader(new FileReader(file));

			String str = br.readLine();

			while (str != null) {
				logList.add(str);
				str = br.readLine();
			}
			br.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return logList;
	}

	public void registerLogData(List<String> logList) {

		String phoneNumber = null;
		List<String> surviceCode = new ArrayList<String>();
		List<String> survicePhone = new ArrayList<String>();
		phoneNumbers = new ArrayList<String>();
		surviceCodeMap = new HashMap<String, List<String>>();

		survicePhoneNumber = new HashMap<String, List<String>>();
		callLogMap = new HashMap<String,List<String>>();
		boolean firstLine = true;
		boolean c1Add = false;

		for (String record : logList) {
			switch(record.charAt(0)) {
			case '1' :
				phoneNumber = record.substring(2);
				phoneNumbers.add(phoneNumber); //利用者の電話番号登録(全体のキー)
				surviceCode = new ArrayList<String>();
				survicePhone = new ArrayList<String>();
				break;
			case '2' :
				String a = record.substring(2, 4);
				if (record.substring(2, 4).equals("C1")) {
					//C1なら後ろの電話番号を登録
					survicePhone.add(record.substring(5));
					if(c1Add==false) {
						surviceCode.add(record.substring(2, 4));
						c1Add = true;
					}
				}
				else {
					surviceCode.add(record.substring(2, 4));
				}
				break;
			case '5' : //通話記録登録
				callLog = new ArrayList<String>();
				callLog.add(record.substring(2,12));
				callLog.add(record.substring(13,18));
				callLog.add(record.substring(19,22));
				callLog.add(record.substring(23));
				callLogMap.put(phoneNumber, callLog);
				break;
			case '0' :
				if(firstLine==true) {
					firstLine=false;
				} else {
					surviceCodeMap.put(phoneNumber, surviceCode);
					survicePhoneNumber.put(phoneNumber, survicePhone);
				}
				break;
				}
		}
	}

	/*public void calculateFee() {
		try {
			for (String phoneNumber : phoneNumbers) {
				//基本料金計算
				int basicFee = 1000;
				boolean familyCam = false;
				boolean afternoonCam = false;
				boolean c1Add = false;

				for (Map.Entry<String,List<String>> scl : surviceCodeMap.entrySet()) {
					if (scl.getKey() == phoneNumber) {
						for (String sc : scl.getValue()) {
							switch(sc) {
							case "C1" :
								if(c1Add==false) {
									basicFee = basicFee + 100;
									familyCam = true;
								}
								break;
							case "E1" :
								basicFee = basicFee + 200;
								afternoonCam = true;
								break;
							default :
								break;
							}
						}
					}
				}

				sdFormat = new SimpleDateFormat("hh:mm");
				Date discountStartTime = sdFormat.parse(disStartTime);
				Date discountEndTime = sdFormat.parse(disEndTime);

				int callFee = 0;

				for (Map.Entry<String, List<String>> cr : callLogMap.entrySet()) {
					List<String> callRecord = cr.getValue();
					int callTime = Integer.parseInt(callRecord.get(2));
					List<String> discountNumbers = new ArrayList<String>();
					for (Map.Entry<String, List<String>> spn : survicePhoneNumber.entrySet()) {
						if (spn.getKey() == phoneNumber) {
							for (String sp : spn.getValue()) {
								discountNumbers.add(sp);
							}
						}
					}

					if (cr.getKey() == phoneNumber) {
						if(familyCam == false & afternoonCam == false) {
							callFee = callFee + basicCallFeePerMin * callTime;
						} else if (familyCam == true & afternoonCam == false) {
							if (discountNumbers.contains(callRecord.get(3))) {
								callFee = callFee + familyDiscountCallFeePerMin * callTime;
							} else {
								callFee = callFee + basicCallFeePerMin * callTime;
							}
						} else if (familyCam == false & afternoonCam == true) {
							Date startTime = sdFormat.parse(callRecord.get(2));

							if (discountStartTime.after(startTime) & discountEndTime.before(startTime)) {
								callFee = callFee + afternoonDiscountCallFeePerMin * callTime;
							} else {
								callFee = callFee + basicCallFeePerMin * callTime;
							}
						} else {
							Date startTime = sdFormat.parse(callRecord.get(2));

							if (discountStartTime.after(startTime) & discountEndTime.before(startTime)) {
								if (discountNumbers.contains(callRecord.get(3))) {
									callFee = callFee + twoDiscountsCallFeePerMin * callTime;
								} else {
									callFee = callFee + afternoonDiscountCallFeePerMin * callTime;
								}
							} else {
								if (discountNumbers.contains(callRecord.get(3))) {
									callFee = callFee + familyDiscountCallFeePerMin * callTime;
								} else {
									callFee = callFee + basicCallFeePerMin * callTime;
								}
							}
						}
					}
				}

				File file = new File(outputFilePath);
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));

				pw.print(phoneNumberNo + " " + phoneNumber + "\n");
				pw.print(basicFeeNo + " " + basicFee + "\n");
				pw.print(callFeeNo + " " + callFee + "\n");
				pw.print(endLineNo + " " + endLine + "\n");

				pw.close();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
}
