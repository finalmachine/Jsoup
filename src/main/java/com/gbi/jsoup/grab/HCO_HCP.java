package com.gbi.jsoup.grab;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gbi.commons.net.http.SimpleHttpClient;
import com.gbi.commons.net.http.SimpleHttpResponse;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class HCO_HCP {

	private final static String entryUrl = "http://www.guahao.com/search/hospitals?q=";
	private final static String HCO_table = "test_HCO";
	private final static String HCP_table = "test_HCP";

	private static DBCollection collection1 = null;
	private static DBCollection collection2 = null;

	static {
		MongoClient c = null;
		try {
			c = new MongoClient();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		collection1 = c.getDB("test").getCollection(HCO_table);
		collection2 = c.getDB("test").getCollection(HCP_table);
	}

	private static void run() throws UnsupportedEncodingException {
		SimpleHttpClient client = new SimpleHttpClient();
		String searchName = "华山医院";
		SimpleHttpResponse response = client.get(entryUrl + URLEncoder.encode(searchName, "UTF-8"));

		if (response != null) {
			Elements lis = response.getDocument().select("ul.hos_ul>li");
			if (lis.size() == 0) {
				System.out.println("没有搜索到有效的结果:" + searchName);
				client.close();
				return;// TODO
			}
			for (Element li : lis) {
				Element dl = li.select("div>div[class=info g-left]>dl").first();
				DBObject hco = new BasicDBObject();
				hco.put("_id", dl.select("dt>a").text().trim());
				hco.put("level", dl.select("dt>em").text().trim());
				hco.put("contact", dl.select("dd>p.tel").text().trim());
				hco.put("address", dl.select("dd>p.addr").text().trim());
				// TODO 存入数据库
				collection1.save(hco);
				grab1(dl.select("dt>a").attr("href"));
				break; // TODO
			}
		} else {
			System.err.println("error search " + searchName);
			client.close();
			return;// TODO
		}
		client.close();
	}

	public static boolean grab1(String url) {
		SimpleHttpClient client = new SimpleHttpClient();
		SimpleHttpResponse response = client.get(url);
		if (response == null) {
			System.err.println("the hospital can't grab");
		}
		Elements lis = response.getDocument().select(
				"div[class=content-wrap content-department js-departments]>ul>li:gt(0)");
		for (Element li : lis) {
			for (Element span : li.select("span")) {
				response = client.get(span.select("a").attr("href"));
				if (response == null) {
					System.out.println("丢失一个科室");
					continue;
				}
				Element div = response.getDocument()
						.select("div[class=g-hddoctor-list g-clear js-tab-content]>div.more").first();
				if (div == null) {// 不用点击更多
					Elements divs = response
							.getDocument()
							.select("div[class=g-hddoctor-list g-clear js-tab-content]>div[class=g-clear g-doc-info]");
					for (Element tempDiv : divs) {
						response = client.get(tempDiv.select("a").attr("href"));
						if (response == null) {
							System.out.println("丢失一条医生");
							continue;
						}
						grab2(response.getDocument());
					}
				} else { // 点击更多
					response = client.get(div.select("a").attr("href"));
					if (response == null) {
						System.out.println("点击更多出现问题");
						continue;
					}
					if (response.getDocument().select("span.pd>label").first() == null) { // 只有一页
						Elements lis2 = response.getDocument().select("ul#J_ExpertList>li");
						for (Element li2 : lis2) {
							response = client.get(li2.select("div[class=doc-base-info]>a")
									.first().attr("href"));
							if (response == null) {
								System.out.println("丢失一条医生");
								continue;
							}
							grab2(response.getDocument());
						}
					} else { // 有多页
						int page = Integer.parseInt(response.getDocument().select("span.pd>label").first().text());
						String uri = response.getUrl();
						for (int i = 1; i <= page; ++i) {
							response = client.get(uri + "?pageNo=" + i);
							if (response == null) {
								System.out.println("丢失一页");
								continue;
							}
							Elements lis2 = response.getDocument().select("ul#J_ExpertList>li");
							for (Element li2 : lis2) {
								response = client.get(li2.select("div[class=doc-base-info]>a")
										.first().attr("href"));
								if (response == null) {
									System.out.println("丢失一条医生");
									continue;
								}
								grab2(response.getDocument());
							}
						}
					}
				}
			}
			System.out.println();//TODO
		}
		client.close();
		return true;
	}

	public static void grab2(Document dom) {
		if (dom.select("div[class=msg g-left]>h1>label").text().trim().length() == 0) {
			return;
		}
		DBObject doctor = new BasicDBObject();
		Element div = dom.select("div[class=msg g-left]").first();
		doctor.put("_id", dom.select("a#addFavBtn").attr("data-id"));
		doctor.put("name", div.select("h1>strong").text().trim());
		doctor.put("title", div.select("h1>label").text().trim());
		doctor.put("hospital", div.select("div>p>a").first().text().trim());
		doctor.put("department", div.select("div>p>a").last().text().trim());

		div = dom.select("div[class=introduce g-left]").first();
		Elements as = div.select(">a");
		BasicDBList list = new BasicDBList();
		for (Element a : as) {
			list.add(a.text().trim());
		}
		doctor.put("disease", list);

		div = dom.select("div[class=skill-msg]").first();
		if (div.select("span[class=more-box hide]").first() == null) {
			doctor.put("advantage", div.select("div[class=skill-msg]>span").text());
		} else {
			doctor.put("advantage", div.select("span[class=more-content]").first().text());
		}

		div = dom.select("div[class=word-msg]").first();
		if (div.select("span[class=more-box hide]").first() == null) {
			doctor.put("resume", div.select("div[class=word-msg]>span").text());
		} else {
			doctor.put("resume", div.select("span[class=more-content]").first().text());
		}
		// TODO 存入数据库
		collection2.save(doctor);
		System.out.println(doctor);
	}

	public static void main(String[] args) throws Exception {
		run();
	}
}
