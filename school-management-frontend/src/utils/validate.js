/**
 * 身份证号码校验
 * @param {string} idcard 身份证号码
 * @returns {boolean} 是否有效
 */
export function validateIDCard(idcard) {
  if (!idcard) return false;
  // 15位和18位身份证号码的正则表达式
  const regIdCard = /^(^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$)|(^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])((\d{4})|\d{3}[Xx])$)$/;
  // 如果通过该验证，说明身份证格式正确，但准确性还需计算
  if (regIdCard.test(idcard)) {
    if (idcard.length == 18) {
      const idCardWi = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2]; // 加权因子
      const idCardY = [1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2]; // 校验码
      let idCardWiSum = 0; // 用来保存前17位各自乖以加权因子后的总和
      for (let i = 0; i < 17; i++) {
        idCardWiSum += parseInt(idcard.substring(i, i + 1)) * idCardWi[i];
      }
      const idCardMod = idCardWiSum % 11; // 计算出校验码所在数组的位置
      const idCardLast = idcard.substring(17); // 得到最后一位身份证号码
      // 如果等于 raiesult[idCardMod] ，说明校验码是正确的
      if (idCardLast.toUpperCase() == idCardY[idCardMod]) {
        return true;
      } else {
        return false;
      }
    } else {
        // 15位身份证号码基本有效
        return true;
    }
  } else {
    return false;
  }
}

/**
 * 从身份证号码中提取出生日期
 * @param {string} idcard 身份证号码
 * @returns {string|null} 出生日期 (YYYY-MM-DD) 或 null
 */
export function getBirthDateFromIdCard(idcard) {
  if (!validateIDCard(idcard)) {
    return null;
  }
  let year = '';
  let month = '';
  let day = '';
  if (idcard.length === 18) {
    year = idcard.substring(6, 10);
    month = idcard.substring(10, 12);
    day = idcard.substring(12, 14);
  } else if (idcard.length === 15) {
    year = '19' + idcard.substring(6, 8);
    month = idcard.substring(8, 10);
    day = idcard.substring(10, 12);
  }
  // 简单校验日期合法性 (可进一步完善)
  const dateStr = `${year}-${month}-${day}`;
  if (isNaN(Date.parse(dateStr))) { 
    return null; 
  }
  return dateStr;
}

/**
 * 从身份证号码中提取性别
 * @param {string} idcard 身份证号码
 * @returns {string|null} 性别 ('男', '女') 或 null
 */
export function getGenderFromIdCard(idcard) {
  if (!validateIDCard(idcard)) {
    return null;
  }
  let genderCode = '';
  if (idcard.length === 18) {
    genderCode = idcard.substring(16, 17);
  } else if (idcard.length === 15) {
    genderCode = idcard.substring(14, 15);
  }
  // 奇数为男，偶数为女
  return parseInt(genderCode) % 2 === 1 ? '男' : '女';
} 