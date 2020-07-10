package com.ljj.crawler.endpoint;

import com.ljj.core.constant.ExtractType;
import org.junit.jupiter.api.Test;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/10
 **/
public class ParseConfigDemo {

    String selector = "body > div:nth-child(2) > table > tbody > tr.a1 > th > div.tdtit";
    ExtractType extractType = ExtractType.CSS;

    @Test
    public void testCssSelector() {


    }


    String content = "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n" +
            "<html>\n" +
            "\t<head>\n" +
            "\t\t<base href=\"http://xkz.cbirc.gov.cn:80/ilicence/\">\n" +
            "\t\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=GBK\">\n" +
            "\t\t<title>中国银行保险监督管理委员会 金融许可证信息</title>\n" +
            "\t\t<link href=\"/ilicence/css/styleDetail.css\" rel=\"stylesheet\" type=\"text/css\" />\n" +
            "\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"/ilicence/ext/resources/css/ext-all.css\" />\n" +
            "\t\t<style type=\"text/css\">\n" +
            "td {\n" +
            "\tpadding-left: 5px;\n" +
            "\tfont-size: 17px;\n" +
            "}\n" +
            "\n" +
            "</style>\n" +
            "\t\t<script type=\"text/javascript\">\n" +
            "\tvar px = 'up';\n" +
            "\tfunction change(){\n" +
            "\t\tif(px=='up'){\n" +
            "\t\t\tdocument.getElementById(\"px\").src = '/ilicence/image/down.gif';\n" +
            "\t\t\tfor(var i=0;i<4;i++){\n" +
            "\t\t\t\tdocument.getElementById(\"td\"+i).style.display = \"none\";\n" +
            "\t\t\t}\n" +
            "\t\t\tpx='down';\n" +
            "\t\t}else{\n" +
            "\t\t\tdocument.getElementById(\"px\").src = '/ilicence/image/up.gif';\n" +
            "\t\t\tfor(var i=0;i<4;i++){\n" +
            "\t\t\t\tdocument.getElementById(\"td\"+i).style.display = \"\";\n" +
            "\t\t\t}\n" +
            "\t\t\tpx='up';\n" +
            "\t\t}\n" +
            "\t}\n" +
            "</script>\n" +
            "\t</head>\n" +
            "\n" +
            "\t<body class=\"trw-body\">\n" +
            "\t\t\n" +
            "\n" +
            "\n" +
            "\n" +
            "<div class=\"log\" style=\"background: url(image/logobg.jpg);background-repeat: repeat-x;height:85px;\">\n" +
            "            <div style=\"position:relative;width:1024px;height:85px;margin:0 auto;background: url(image/logo-n.jpg);background-repeat: no-repeat;\">\n" +
            "                \n" +
            "                <div class=\"top_publish\" id=\"top_publish\" unselectable=\"on\">\n" +
            "\t                \n" +
            "\t                                发布时间：<br />2020年07月06日\n" +
            "\t                \n" +
            "                </div>                \n" +
            "            </div>\n" +
            "        </div>\n" +
            "\n" +
            "\t\t<div style=\"margin-top: 10px; margin-bottom: 10px;\">\n" +
            "\t\t\t<table width=\"1024\" border=\"0\" align=\"center\" cellpadding=\"0\"\n" +
            "\t\t\t\tcellspacing=\"0\" class=\"trw-table-s1\">\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t<tr class=\"a1\">\n" +
            "\t\t\t\t\t<th colspan=\"2\" align=\"center\">\n" +
            "\t\t\t\t\t\t<div class=\"tdpt\">\n" +
            "\t\t\t\t\t\t\t<img src=\"/ilicence/image/icon.gif\"\n" +
            "\t\t\t\t\t\t\t\twidth=\"24\" height=\"24\">\n" +
            "\t\t\t\t\t\t</div>\n" +
            "\t\t\t\t\t\t<div class=\"tdtit\">\n" +
            "\t\t\t\t\t\t\t<font color=\"#454BBA\"><b>机构详细信息</b> </font>\n" +
            "\t\t\t\t\t\t</div>\n" +
            "\t\t\t\t\t</th>\n" +
            "\t\t\t\t</tr> \n" +
            "\t\t\t\t\n" +
            "\t\t\t\t<tr class=\"a0\">\n" +
            "\t\t\t\t\t<td width=\"35%\" align=\"right\" height=\"25\">\n" +
            "\t\t\t\t\t\t机构编码：\n" +
            "\t\t\t\t\t</td>\n" +
            "\t\t\t\t\t<td>\n" +
            "\t\t\t\t\t\tB0107S261010077\n" +
            "\t\t\t\t\t</td>\n" +
            "\t\t\t\t</tr>\n" +
            "\t\t\t\t<tr class=\"a0\">\n" +
            "\t\t\t\t\t<td align=\"right\" height=\"25\">\n" +
            "\t\t\t\t\t\t机构名称：\n" +
            "\t\t\t\t\t</td>\n" +
            "\t\t\t\t\t<td>\n" +
            "\t\t\t\t\t\t北京银行股份有限公司西安昆明路小微支行\n" +
            "\t\t\t\t\t</td>\n" +
            "\t\t\t\t</tr>\n" +
            "\t\t\t\t<tr class=\"a0\">\n" +
            "\t\t\t\t\t<td align=\"right\" height=\"25\">\n" +
            "\t\t\t\t\t\t机构简称：\n" +
            "\t\t\t\t\t</td>\n" +
            "\t\t\t\t\t<td>\n" +
            "\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</td>\n" +
            "\t\t\t\t</tr>\n" +
            "\t\t\t\t<tr class=\"a0\">\n" +
            "\t\t\t\t\t<td align=\"right\" height=\"25\">\n" +
            "\t\t\t\t\t\t机构地址：\n" +
            "\t\t\t\t\t</td>\n" +
            "\t\t\t\t\t<td>\n" +
            "\t\t\t\t\t\t西安市西咸新区沣东新城和平路以西、广场南路以南中海·昆明路九号1幢2单元1层20101、20102、20103号\n" +
            "\t\t\t\t\t</td>\n" +
            "\t\t\t\t</tr>\n" +
            "\t\t\t\t<tr class=\"a0\">\n" +
            "\t\t\t\t\t<td align=\"right\" height=\"25\">\n" +
            "\t\t\t\t\t\t经度：\n" +
            "\t\t\t\t\t</td>\n" +
            "\t\t\t\t\t<td>\n" +
            "\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</td>\n" +
            "\t\t\t\t</tr>\n" +
            "\t\t\t\t<tr class=\"a0\">\n" +
            "\t\t\t\t\t<td align=\"right\" height=\"25\">\n" +
            "\t\t\t\t\t\t纬度：\n" +
            "\t\t\t\t\t</td>\n" +
            "\t\t\t\t\t<td>\n" +
            "\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</td>\n" +
            "\t\t\t\t</tr>\n" +
            "\t\t\t\t<tr class=\"a0\">\n" +
            "\t\t\t\t\t<td align=\"right\" height=\"25\">\n" +
            "\t\t\t\t\t\t机构所在地：\n" +
            "\t\t\t\t\t</td>\n" +
            "\t\t\t\t\t<td>\n" +
            "\t\t\t\t\t\t陕西省-西安市\n" +
            "\t\t\t\t\t</td>\n" +
            "\t\t\t\t</tr>\n" +
            "\t\t\t\t<tr class=\"a0\">\n" +
            "\t\t\t\t\t<td align=\"right\" height=\"25\">\n" +
            "\t\t\t\t\t\t邮政编码：\n" +
            "\t\t\t\t\t</td>\n" +
            "\t\t\t\t\t<td>\n" +
            "\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</td>\n" +
            "\t\t\t\t</tr>\n" +
            "\t\t\t\t<tr class=\"a0\">\n" +
            "\t\t\t\t\t<td align=\"right\" height=\"25\">\n" +
            "\t\t\t\t\t\t发证日期：\n" +
            "\t\t\t\t\t</td>\n" +
            "\t\t\t\t\t<td>\n" +
            "\t\t\t\t\t\t2020-07-03\n" +
            "\t\t\t\t\t</td>\n" +
            "\t\t\t\t</tr>\n" +
            "\t\t\t\t<tr class=\"a0\">\n" +
            "\t\t\t\t\t<td align=\"right\" height=\"25\">\n" +
            "\t\t\t\t\t\t批准成立日期：\n" +
            "\t\t\t\t\t</td>\n" +
            "\t\t\t\t\t<td>\n" +
            "\t\t\t\t\t\t2020-06-28\n" +
            "\t\t\t\t\t</td>\n" +
            "\t\t\t\t</tr>\n" +
            "\t\t\t\t<tr class=\"a0\">\n" +
            "\t\t\t\t\t<td align=\"right\" height=\"25\">\n" +
            "\t\t\t\t\t\t发证机关：\n" +
            "\t\t\t\t\t</td>\n" +
            "\t\t\t\t\t<td>\n" +
            "\t\t\t\t\t\t陕西银监局\n" +
            "\t\t\t\t\t</td>\n" +
            "\t\t\t\t</tr>\n" +
            "\t\t\t\t<tr class=\"a0\">\n" +
            "\t\t\t\t\t<td align=\"right\" height=\"25\">\n" +
            "\t\t\t\t\t\t流水号：\n" +
            "\t\t\t\t\t</td>\n" +
            "\t\t\t\t\t<td>\n" +
            "\t\t\t\t\t\t00667993\n" +
            "\t\t\t\t\t</td>\n" +
            "\t\t\t\t</tr>\n" +
            "\t\t\t\t<tr class=\"a0\">\n" +
            "\t\t\t\t\t<td colspan=\"2\" align=\"left\" height=\"25\">\n" +
            "\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t&nbsp;&nbsp;&nbsp;&nbsp;许可该机构经营中国银行业监督管理委员会依照有关法律、行政法规和其他规定批准的业务，经营范围以批准文件所列的为准。\n" +
            "\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</td>\n" +
            "\n" +
            "\t\t\t\t</tr>\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t<!-- <tr class=\"a0\">\n" +
            "\t\t\t\t\t<td colspan=\"2\" align=\"center\" height=\"25\">\n" +
            "\t\t\t\t\t\t<input type=\"button\" name=\"close\" value=\"关 闭\"\n" +
            "\t\t\t\t\t\t\tonclick=\"window.close()\" class=\"trw-form-btn1\">\n" +
            "\t\t\t\t\t</td>\n" +
            "\t\t\t\t</tr> -->\n" +
            "\t\t\t\t\n" +
            "\t\t\t</table>\n" +
            "\t\t</div>\n" +
            "\t\t\n" +
            "<div class=\"searchblock\" id = \"bottom1\" style=\"border-top: 1px;\" align=\"center\">\n" +
            "    <div style=\"width:1024px;margin:0 auto;\" align=\"left\">\n" +
            "\t\t注：本系统中所称“发证日期”是监管部门对金融机构颁（换）发许可证的制证日期；“批准成立日期”为监管部门批准该机构设立的日期，金融机构自此具有开展金融业务的一般资质，其他具体新增业务以监管部门的相关文件为准。\n" +
            "\t\t<span id =\"bot_lz0\" style=\"display:inline;\">另注红色流水号为失控证，正在补办中；机构编码为红色的，其许可证被银监会发证机关暂扣。</span>\n" +
            "\t\t<span id =\"bot_lz1\" style=\"display:none;\">另注此数据为近期一年机构设立情况。</span>\n" +
            "\t\t<span id =\"bot_lz2\" style=\"display:none;\">另注此数据为近期二年机构退出列表。</span>\n" +
            "    </div>\n" +
            "</div>\n" +
            "<div class=\"searchblock\" id = \"bottom2\" style=\"border-top: 1px;display: none;\" align=\"center\">\n" +
            "    <div style=\"width:1024px;margin:0 auto;\" align=\"left\">\n" +
            "\t   注：与此表流水号相符金融许可证已失控，已被监管部门确定为无效证，特此向社会公告。\n" +
            "\t</div>\n" +
            "</div>\n" +
            "\t\t<script type=\"text/javascript\">\n" +
            "\t\t\n" +
            "\t\t\tfor(var i=0;i<3;i++){\n" +
            "\t\t\t\tdocument.getElementById(\"bot_lz\"+i).style.display = 'none';\n" +
            "\t\t\t}\n" +
            "\t\t\tdocument.getElementById(\"bottom1\").style.display = 'block';\n" +
            "\t\t\tdocument.getElementById(\"bottom2\").style.display = 'none';\n" +
            "\t\t\n" +
            "\t\t</script>\n" +
            "\t</body>\n" +
            "</html>\n";
}
