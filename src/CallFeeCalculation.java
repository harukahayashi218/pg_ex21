import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallFeeCalculation {

	List<String> phoneNumbers;
	Map<String, List<String>> surviceCodeMap;
	Map<String, List<String>> survicePhoneNumber;

	List<String> callLog;
	Map<String, List<String>> callLogMap;

	String inputFilePath = "C:\\Users\\hayashi\\Desktop\\課題#21\\record.log";
	String outputFilePath = "C:\\Users\\hayashi\\Desktop\\課題#21\\invoice.dat";
	int basicCallFeePerMin = 20;
	int familyDiscountCallFeePerMin = 10;
	int afternoonDiscountCallFeePerMin = 15;
	int twoDiscountsCallFeePerMin = 7;
	SimpleDateFormat sdFormat;
	String phoneNumberNo = "1";
	String basicFeeNo = "5";
	String callFeeNo = "7";
	String endLineNo = "9";
	String endLine = "====================";
	String disStartTime = "08:00";
	String disEndTime = "17:59";

	public static void main(String[] args) {

		CallFeeCalculation ccc = new CallFeeCalculation();
		List<String> logList = ccc.readLog();
		ccc.registerLogData(logList);
		ccc.calculateFee();
	}

	// 全行格納
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
		} catch (IOException e) {
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
		callLogMap = new HashMap<String, List<String>>();
		boolean firstLine = true;
		boolean c1Add = false;

		for (String record : logList) {
			switch (record.charAt(0)) {
			case '1':
				phoneNumber = record.substring(2);
				phoneNumbers.add(phoneNumber); // 利用者の電話番号登録(全体のキー)
				surviceCode = new ArrayList<String>();
				survicePhone = new ArrayList<String>();
				break;
			case '2':
				String a = record.substring(2, 4);
				if (record.substring(2, 4).equals("C1")) {
					// C1なら後ろの電話番号を登録
					survicePhone.add(record.substring(5));
					if (c1Add == false) {
						surviceCode.add(record.substring(2, 4));
						c1Add = true;
					}
				} else {
					surviceCode.add(record.substring(2, 4));
				}
				break;
			case '5': // 通話記録登録

				if(callLogMap.containsKey(phoneNumber)) {
					List<String> temp = callLogMap.get(phoneNumber);
					temp.add("|");
					temp.add(record.substring(2, 12));
					temp.add(record.substring(13, 18));
					temp.add(record.substring(19, 22));
					temp.add(record.substring(23));
					callLogMap.put(phoneNumber, temp);
				} else {
					callLog = new ArrayList<String>();
					callLog.add(record.substring(2, 12));
					callLog.add(record.substring(13, 18));
					callLog.add(record.substring(19, 22));
					callLog.add(record.substring(23));
					callLogMap.put(phoneNumber, callLog);
				}
				break;
			case '0':
				if (firstLine == true) {
					firstLine = false;
				} else {
					surviceCodeMap.put(phoneNumber, surviceCode);
					survicePhoneNumber.put(phoneNumber, survicePhone);
					c1Add = false;
				}
				break;
			}
		}
	}

	public Fee calculateBasicFee(String phoneNumber) {

		Fee bf = new Fee();

		for (Map.Entry<String,List<String>> scl : surviceCodeMap.entrySet()) {
			if (scl.getKey().equals(phoneNumber)) {
				for (String sc : scl.getValue()) {
					switch(sc) {
					case "C1" :
						if(bf.c1Add==false) {
							bf.basicFee = bf.basicFee + 100;
							bf.familyCam = true;
							bf.c1Add = true;
						}
						break;
					case "E1" :
						bf.basicFee = bf.basicFee + 200;
						bf.afternoonCam = true;
						break;
					default :
						break;
					}
				}
			}
		}
		return bf;
	}

	public void calculateFee() {
		try {
			for (String phoneNumber : phoneNumbers) {
				// 基本料金計算
				Fee bf = calculateBasicFee(phoneNumber);
				for (Map.Entry<String, List<String>> cr : callLogMap.entrySet()) {
					if (cr.getKey().equals(phoneNumber)) { // 下の処理もこの中に

						// 通話記録取得 |で区切られて1つのlistに収まっているものを再list化
						List<String> callRecord = cr.getValue();
						boolean addOk = false;
						List<String> tempList =  new ArrayList<String>();
						List<List<String>> newCallRecord = new ArrayList<List<String>>();
						for (int i = 0; i < callRecord.size(); i++) {
							if (addOk == false) {
								tempList = new ArrayList<String>();
								tempList.add(callRecord.get(i));
								addOk = true;
							} else {
								if (callRecord.get(i).equals("|")) {
									newCallRecord.add(tempList);
									addOk = false;
								} else {
									tempList.add(callRecord.get(i));
								}
							}
						}

						// 割引対象番号取得
						List<String> discountNumbers = new ArrayList<String>();
						for (Map.Entry<String, List<String>> spn : survicePhoneNumber.entrySet()) {
							if (spn.getKey().equals(phoneNumber)) {
								for (String sp : spn.getValue()) {
									discountNumbers.add(sp);
								}
							}
						}

						int callFee = 0;
						for (List<String> l : newCallRecord) {
							int callTime = Integer.parseInt(l.get(2));

							sdFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm");
							String startDate = l.get(0);
							String startTime = l.get(1);
							Date callDate = sdFormat.parse(startDate + " " + startTime);
							Date discountStartTime = sdFormat.parse(startDate + " " + disStartTime);
							Date discountEndTime = sdFormat.parse(startDate + " " + disEndTime);

							if (bf.familyCam == false & bf.afternoonCam == false) {
								callFee = callFee + basicCallFeePerMin * callTime;
							} else if (bf.familyCam == true & bf.afternoonCam == false) {
								if (discountNumbers.contains(callRecord.get(3))) {
									callFee = callFee + familyDiscountCallFeePerMin * callTime;
								} else {
									callFee = callFee + basicCallFeePerMin * callTime;
								}
							} else if (bf.familyCam == false & bf.afternoonCam == true) {

								if (discountStartTime.after(callDate) & discountEndTime.before(callDate)) {
									callFee = callFee + afternoonDiscountCallFeePerMin * callTime;
								} else {
									callFee = callFee + basicCallFeePerMin * callTime;
								}
							} else {

								if (discountStartTime.after(callDate) & discountEndTime.before(callDate)) {
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


						//出力
						//
//						File file = new File(outputFilePath);
//						PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		//
//						pw.print(phoneNumberNo + " " + phoneNumber + "\n");
//						pw.print(basicFeeNo + " " + bf.basicFee + "\n");
//						pw.print(callFeeNo + " " + callFee + "\n");
//						pw.print(endLineNo + " " + endLine + "\n");
		//
//						pw.close();
					}
				}
			}

		} catch (ParseException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {

		}
}
}

