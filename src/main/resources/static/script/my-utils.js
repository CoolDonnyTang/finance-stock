function checkStockCode(code) {
	if(code===null || code===undefined) {
		return false
	}
	var reg = /^((sh)|(sz))\d{6}$/;
	return reg.test(code);
}
function checkIpAddr(addr) {
	if(addr===null || addr===undefined) {
		return false
	}
	var reg = /^((\d)|([1-9]\d)|(1\d{2})|((2[0-4]\d)|(25[0-5])))(\.((\d)|([1-9]\d)|(1\d{2})|((2[0-4]\d)|(25[0-5])))){3}$/;
	return reg.test(addr);
}
function checkIpPort(port) {
	if(checkNum(port) && port<=65535 && port>=0) {
		return true;
	}
	return false;
}
function checkEmail(mail) {
	if(mail===null || mail===undefined) {
		return false
	}
	var reg = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
	return reg.test(mail);
}
function checkNickname(name) {
	if(name===null || name===undefined) {
		return false
	}
	var reg = /^[\u4E00-\u9FA5A-Za-z0-9_]{4,10}$/;
	return reg.test(name);
}
function checkIdNumber(number) {
	if(number===null || number===undefined) {
		return false
	}
	var reg = /^[1-9]\d{5}[1-2]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])(\d{4}|((\d{3}X)|(\d{3}x)))$/;
	return reg.test(number);
}
function checkPassword(pwd) {
	if(pwd===null || pwd===undefined) {
		return false
	}
	var reg = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,16}$/;
	return reg.test(pwd);
}
function checkCheckCode(checkCode) {
	if(checkCode===null || checkCode===undefined) {
		return false
	}
	var reg = /^[a-zA-Z0-9]{5}$/;
	return reg.test(checkCode);
}
function checkInteger(checkCode) {
	if(checkCode===null || checkCode===undefined) {
		return false
	}
	var reg = /^[1-9]\d{0,5}$/;
	return reg.test(checkCode);
}
function checkNum(data) {
	if(data===null || data===undefined) {
		return false
	}
	var reg = /^[1-9]\d*$/;
	return reg.test(data);
}
function checkNum4(data) {
	if(data===null || data===undefined) {
		return false
	}
	var reg = /^[1-9]\d{0,3}$/;
	return reg.test(data);
}
function checkBigDicemal(checkCode) {
	if(checkCode===null || checkCode===undefined) {
		return false
	}
	var reg = /^((0\.(\d|\d{2}))|([1-9]\d{0,9})|([1-9]\d{0,9}\.(\d|\d{2}))|(0))$/;
	return reg.test(checkCode);
}
/**
 * 根据正则表达式获取字符串
 * tangyanrentyr
 * 2017年4月22日
 * @param data
 * @param reg
 * @returns
 */
function getStringByReg(data, reg) {
	if(data.match(reg)===null) {
		return null;
	}
	return data.match(reg)[1];
}
/**
 * 将一个毫秒值转换成 年-月-日 小时：分钟：秒 格式的字符串
 * tangyanrentyr
 * 2017年5月11日
 * @param time
 * @returns {String}
 */
function timeToDate(time) {
	if(undefined==time || null==time) {
		return "";
	}else {
		var date = new Date(time);
		return date.getFullYear() + "-" + toTwoNumber(date.getMonth() + 1) + "-" + toTwoNumber(date.getDate()) + " " + toTwoNumber(date.getHours()) + ":" + toTwoNumber(date.getMinutes()) + ":" + toTwoNumber(date.getSeconds());
	}
}
/**
 * 将一位数转换为 0X 形式的两位数字符串
 * tangyanrentyr
 * 2017年5月11日
 * @param data
 */
function toTwoNumber(data) {
	data = data + "";
	if(data.length === 1) {
		data = "0" + data;
	}
	return data;
}













