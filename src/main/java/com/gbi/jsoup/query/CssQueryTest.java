package com.gbi.jsoup.query;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.gbi.jsoup.util.ResourceUtil;

public class CssQueryTest {

	public static void test1() throws IOException {
		System.out.println("------test1----->\n");
		File file = ResourceUtil.getSelfFile(CssQueryTest.class, "test1.html");
		Document dom = Jsoup.parse(file, "UTF-8");
		System.out.println("div:" + dom.select("div").size());
		System.out.println("div>table:" + dom.select("div>table").size());
		System.out.println("div>table:eq(0):" + dom.select("div>table:eq(0)").size());
		System.out.println("div>table:eq(0)>tbody:" + dom.select("div>table:eq(0)>tbody").size());
		System.out.println("div>table:eq(0)>tbody>tr:" + dom.select("div>table:eq(0)>tbody>tr").size());
		System.out.println("div>table:eq(0)>tbody>tr>td:" + dom.select("div>table:eq(0)>tbody>tr>td").size());
		System.out.println("div>table:eq(0)>tbody>tr>td:gt(0):" + dom.select("div>table:eq(0)>tbody>tr>td:gt(0)").size());
		System.out.println("\n<-----test1------");
	}
	
	public static void test2() throws IOException {
		System.out.println("------test2----->\n");
		File file = ResourceUtil.getSelfFile(CssQueryTest.class, "test2.html");
		Document dom = Jsoup.parse(file, "UTF-8");
		System.out.println("div:eq(0):");
		for (Element e : dom.select("div:eq(0)")) {
			System.out.println(e.id());
		}
		System.out.println("div:eq(1):");
		for (Element e : dom.select("div:eq(1)")) {
			System.out.println(e.id());
		}
		System.out.println("div:eq(2):");
		for (Element e : dom.select("div:eq(2)")) {
			System.out.println(e.id());
		}
		System.out.println("div:eq(3):");
		for (Element e : dom.select("div:eq(3)")) {
			System.out.println(e.id());
		}
		System.out.println("\n<-----test2------");
	}
	
	public static void test3() throws IOException {
		System.out.println("------test3----->\n");
		File file = ResourceUtil.getSelfFile(CssQueryTest.class, "test3.html");
		Document dom = Jsoup.parse(file, "UTF-8");
		System.out.println("size of 'body div':" + dom.select("body div").size());
		System.out.println("size of 'body>div':" + dom.select("body>div").size());
		System.out.println("\n<-----test3------");
	}
	
	public static void test4() throws IOException {
		System.out.println("------test4----->\n");
		File file = ResourceUtil.getSelfFile(CssQueryTest.class, "test4.html");
		Document dom = Jsoup.parse(file, "UTF-8");
		System.out.println("div>div+table:" + dom.select("div>div+table").first().id());
		System.out.println("div>div+table+span:" + dom.select("div>div+table+span").first().id());
		System.out.println("div>div~span:" + dom.select("div>div~span").first().id());
		System.out.println("div>div,div>table,div>span:" + dom.select("div>div,div>table,div>span"));
		System.out.println("\n<-----test4------");
	}
	
	public static void test5() throws IOException {
		System.out.println("------test5----->\n");
		File file = ResourceUtil.getSelfFile(CssQueryTest.class, "test5.html");
		Document dom = Jsoup.parse(file, "UTF-8");
		Element element = dom.select("meta[http-equiv=Content-Type][content*=charset]").first();
		System.out.println(element == null);
		System.out.println(element);
		System.out.println("\n<-----test5------");
	}
	
	public static void test6() throws IOException {
		
	}
	
	public static void main(String[] args) throws IOException {
		test1();
		test2();
		test3();
		test4();
		test5();
		test6();
	}
}
