package koh.look;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import koh.protocol.types.game.look.EntityLook;
import koh.protocol.types.game.look.SubEntity;
import koh.utils.Couple;
import koh.utils.StringExtensions;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Melancholia
 */
public class EntityLookParser {

    public static final int CURRENT_FORMAT_VERSION = 0;
    public static final int DEFAULT_NUMBER_BASE = 10;

    public static EntityLook copy(EntityLook entityLook) {
        return new EntityLook(entityLook.bonesId, new ArrayList<>(entityLook.skins), new ArrayList<>(entityLook.indexedColors), new ArrayList<>(entityLook.scales),
                new ArrayList<SubEntity>() {
                    {
                        entityLook.subentities.forEach(sub -> this.add(new SubEntity(sub.bindingPointCategory, sub.bindingPointIndex, copy(sub.subEntityLook))));
                    }
                });
    }

    private static Couple<Integer, Integer> ExtractIndexedColor(int indexedColor) {
        return new Couple<Integer, Integer>(indexedColor >> 24, indexedColor & 16777215);
    }

    private static int parseIndexedColor(String str) {
        int length = str.indexOf("=");
        boolean flag = (int) str.charAt(length + 1) == 35;
        return Integer.parseInt(str.substring(0, length)) << 24 | (flag ? Integer.parseInt(str.substring(length + (flag ? 2 : 1), str.length() - (length + (flag ? 2 : 1))), 16) : Integer.parseInt(str.substring(length + (flag ? 2 : 1), str.length() - (length + (flag ? 2 : 1)))));
    }

    public static EntityLook fromString(String str, int pFormatVersion, int pNumberBase) {
        return fromString(str, pFormatVersion, pNumberBase, null);
    }

    public static EntityLook fromString(String str, int pFormatVersion) {
        return fromString(str, pFormatVersion, 10, null);
    }

    public static EntityLook fromString(String str) {
        return fromString(str, 0, 10, null);
    }

    public static EntityLook fromString(String str, int pFormatVersion, int pNumberBase, EntityLook tiphonInstance) {
        String headersStr;
        String[] headers;
        String[] skins;
        String[] colors;
        String[] colorPair;
        int colorIndex;
        int colorValue;
        String[] scales;
        short commonScale;
        String subEntitiesStr;
        int i;
        List<String> subEntities;
        int subEnd;
        String subEntityHeader;
        String subEntityBody;
        String[] subEntityBinding;
        int bindingCategory;
        int bindingIndex;
        EntityLook el = ((tiphonInstance != null) ? tiphonInstance : new EntityLook());
        //el.lock();
        int formatVersion = CURRENT_FORMAT_VERSION;
        int numberBase = DEFAULT_NUMBER_BASE;
        if (str.charAt(0) == '[') {
            headersStr = str.substring(1, str.indexOf("]"));
            if (headersStr.indexOf(",") > 0) {
                headers = headersStr.split(",");
                if (headers.length != 2) {
                    throw (new Error("Malformated headers in an Entity Look string."));
                };
                formatVersion = Integer.parseInt(headers[0]);
                numberBase = getNumberBase(headers[1]);
            } else {
                formatVersion = Integer.parseInt(headersStr);
            }
            str = str.substring((str.indexOf("]") + 1));
        }
        if (((!((str.charAt(0) == '{'))) || (!((str.charAt((str.length() - 1)) == '}'))))) {
            throw (new Error("Malformed body in an Entity Look string."));
        }
        str = str.substring(1, (str.length() - 1));
        String[] body = str.split("\\|");
        el.bonesId = Short.parseShort(body[0], numberBase);
        if ((((body.length > 1)) && ((body[1].length() > 0)))) {
            skins = body[1].split(",");
            for (String skin : skins) {
                el.skins.add(Short.parseShort(skin, numberBase));
            }
        }
        if ((((body.length > 2)) && ((body[2].length() > 0)))) {
            colors = body[2].split(",");
            for (String color : colors) {
                colorPair = color.split("=");
                if (colorPair.length != 2) {
                    throw (new Error("Malformed color in an Entity Look string."));
                }
                colorIndex = Integer.parseInt(colorPair[0], numberBase);
                colorValue = 0;
                if (colorPair[1].charAt(0) == '#') {
                    colorValue = Integer.parseInt(colorPair[1].substring(1), 16);
                } else {
                    colorValue = Integer.parseInt(colorPair[1], numberBase);
                }
                colorValue = (((colorIndex & 0xFF) << 24) | (colorValue & 0xFFFFFF));
                el.setColor(colorIndex, colorValue);
            }
        }
        if ((((body.length > 3)) && ((body[3].length() > 0)))) {
            scales = body[3].split(",");
            if (scales.length == 1) {
                commonScale = ((short) (Integer.parseInt(scales[0], numberBase)));
                el.setScales(commonScale, commonScale);
            } else {
                if (scales.length == 2) {
                    el.setScales((Integer.parseInt(scales[0], numberBase)), (short) (Integer.parseInt(scales[1], numberBase)));
                } else {
                    throw (new Error("Malformed scale in an Entity Look string."));
                }
            }
        } else {
            el.setScales(100, (short) 100);
        }
        if ((((body.length > 4)) && ((body[4].length() > 0)))) {
            subEntitiesStr = "";
            i = 4;
            while (i < body.length) {
                subEntitiesStr = (subEntitiesStr + (body[i] + "|"));
                i++;
            }
            subEntitiesStr = subEntitiesStr.substring(0, (subEntitiesStr.length() - 1));
            subEntities = new ArrayList<>();
            while (true) {
                subEnd = subEntitiesStr.indexOf("}");
                if (subEnd == -1) {
                    break;
                }
                subEntities.add(subEntitiesStr.substring(0, (subEnd + 1)));
                //System.out.println(subEntitiesStr.substring(0, (subEnd + 1));
                subEntitiesStr = subEntitiesStr.substring((subEnd + 1));
            }
            for (String subEntity : subEntities) {
                subEntityHeader = subEntity.substring(0, subEntity.indexOf("="));
                subEntityBody = subEntity.substring((subEntity.indexOf("=") + 1));
                subEntityBinding = subEntityHeader.split("@");
                if (subEntityBinding.length != 2) {
                    throw (new Error("Malformed subentity binding in an Entity Look string."));
                }
                if (subEntityBinding[0].startsWith(",")) {
                    subEntityBinding[0] = subEntityBinding[0].substring(1);
                }
                bindingCategory = Integer.parseInt(subEntityBinding[0], numberBase);
                bindingIndex = Integer.parseInt(subEntityBinding[1], numberBase);
                el.addSubEntity(bindingCategory, bindingIndex, EntityLookParser.fromString(subEntityBody, formatVersion, numberBase));
            }
        }
        //el.unlock(true);
        return (el);
    }

    public static String convertToString(EntityLook entityLook) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        int num1 = 0;
        stringBuilder.append(entityLook.bonesId);
        int num2;
        if (entityLook.skins == null || Iterables.size(entityLook.skins) <= 0) {
            num2 = num1 + 1;
        } else {
            stringBuilder.append(StringExtensions.ConcatCopy("|", num1 + 1));
            num2 = 0;
            stringBuilder.append(StringUtils.join(entityLook.skins, ","));
        }
        int num3;
        if (entityLook.indexedColors == null || Iterables.size(entityLook.indexedColors) <= 0) {
            num3 = num2 + 1;
        } else {
            stringBuilder.append(StringExtensions.ConcatCopy("|", num2 + 1));
            num3 = 0;

            String[] array = new String[entityLook.indexedColors.size()];
            for (int i = 0; i < entityLook.indexedColors.size(); i++) {
                array[i] = ExtractIndexedColor(entityLook.indexedColors.get(i)).first + "=" + ExtractIndexedColor(entityLook.indexedColors.get(i)).second;
            }
            stringBuilder.append(StringUtils.join(array, ","));

        }
        int num4;
        if (entityLook.scales == null || entityLook.scales.size() <= 0) {
            num4 = num3 + 1;
        } else {
            stringBuilder.append(StringExtensions.ConcatCopy("|", num3 + 1));
            num4 = 0;
            stringBuilder.append(StringUtils.join(entityLook.scales, ","));
        }
        int num5;
        if (entityLook.subentities == null || entityLook.subentities.isEmpty()) {
            num5 = num4 + 1;
        } else {
            stringBuilder.append(StringExtensions.ConcatCopy("|", num4 + 1));
            num5 = 0;
            String[] array = new String[entityLook.subentities.size()];
            for (int i = 0; i < entityLook.subentities.size(); i++) {
                array[i] = convertToString(entityLook.subentities.get(i));
            }
            stringBuilder.append(StringUtils.join(array, ","));
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    //ParseIndexedColor
    private static Integer[] parseCollectionIntColor(String str) {
        if (str == null || str.isEmpty()) {
            return new Integer[0];
        }
        int startIndex = 0;
        int num = str.indexOf(',', startIndex);
        if (num == -1) {
            return new Integer[]{
                parseIndexedColor(str)
            };
        } else {
            Integer[] objArray = new Integer[StringExtensions.CountOccurences(str, ',', startIndex, str.length() - startIndex) + 1];
            int index = 0;
            while (num != -1) {
                objArray[index] = parseIndexedColor(str.substring(startIndex, num - startIndex));
                startIndex = num + 1;
                num = str.indexOf(',', startIndex);
                ++index;
            }
            objArray[index] = parseIndexedColor(str.substring(startIndex, str.length() - startIndex));
            return objArray;
        }
    }

    private static int[] parseCollectionInt(String str) {
        if (str == null || str.isEmpty()) {
            return new int[0];
        }
        int startIndex = 0;
        int num = str.indexOf(',', startIndex);
        if (num == -1) {
            return new int[]{
                Integer.parseInt(str)
            };
        } else {
            int[] objArray = new int[StringExtensions.CountOccurences(str, ',', startIndex, str.length() - startIndex) + 1];
            int index = 0;
            while (num != -1) {
                objArray[index] = Integer.parseInt(str.substring(startIndex, num - startIndex));
                startIndex = num + 1;
                num = str.indexOf(',', startIndex);
                ++index;
            }
            objArray[index] = Integer.parseInt(str.substring(startIndex, str.length() - startIndex));
            return objArray;
        }
    }

    private static int getNumberBase(String l) {
        switch (l) {
            case "A":
                return (10);
            case "G":
                return (16);
            case "Z":
                return (36);
        };
        throw (new Error((("Unknown number base type '" + l) + "' in an Entity Look string.")));
    }

    private static Short[] parseCollectionShort(String str) {
        if (str == null || str.isEmpty()) {
            return new Short[0];
        }
        int startIndex = 0;
        int num = str.indexOf(',', startIndex);
        if (num == -1) {
            return new Short[]{
                Short.parseShort(str)
            };
        } else {
            Short[] objArray = new Short[StringExtensions.CountOccurences(str, ',', startIndex, str.length() - startIndex) + 1];
            int index = 0;
            while (num != -1) {
                objArray[index] = Short.parseShort(str.substring(startIndex, num - startIndex));
                startIndex = num + 1;
                num = str.indexOf(',', startIndex);
                ++index;
            }
            objArray[index] = Short.parseShort(str.substring(startIndex, str.length() - startIndex));
            return objArray;
        }
    }

    public static EntityLook toEntityLook(String str) {
        if (str == null || str.isEmpty() || (int) str.charAt(0) != 123) {
            throw new Error("Incorrect EntityLook format : " + str);
        }
        int startIndex1 = 1;
        int num1 = str.indexOf('|');
        if (num1 == -1) {
            num1 = str.indexOf("}");
            if (num1 == -1) {
                throw new Error("Incorrect EntityLook format : " + str);
            }
        }

        short bonesId = Short.parseShort(str.substring(startIndex1, num1 - startIndex1));
        int startIndex2 = num1 + 1;
        Short[] numArray1 = new Short[0];
        int num2;
        if ((num2 = str.indexOf('|', startIndex2)) != -1 || (num2 = str.indexOf('}', startIndex2)) != -1) {
            numArray1 = parseCollectionShort(str.substring(startIndex2, num2 - startIndex2));
            startIndex2 = num2 + 1;
        }
        Integer[] numArray2 = new Integer[0];
        int num3;
        if ((num3 = str.indexOf('|', startIndex2)) != -1 || (num3 = str.indexOf('}', startIndex2)) != -1) {
            numArray2 = parseCollectionIntColor(str.substring(startIndex2, num3 - startIndex2));
            startIndex2 = num3 + 1;
        }
        Short[] numArray3 = new Short[0];
        int num4;
        if ((num4 = str.indexOf('|', startIndex2)) != -1 || (num4 = str.indexOf('}', startIndex2)) != -1) {
            numArray3 = parseCollectionShort(str.substring(startIndex2, num4 - startIndex2));
            startIndex2 = num4 + 1;
        }
        ArrayList<SubEntity> list = new ArrayList<>();

        for (int index; startIndex2 < str.length(); startIndex2 = index + 1) {

            int num5 = str.indexOf('@', startIndex2/*, 3*/);
            int num6 = str.indexOf('=', num5 + 1/*, 3*/);
            byte num7 = Byte.parseByte(str.substring(startIndex2, num5 - startIndex2));
            byte num8 = Byte.parseByte(str.substring(num5 + 1, num6 - (num5 + 1)));
            int num9 = 0;
            index = num6 + 1;
            StringBuilder stringBuilder = new StringBuilder();
            do {
                stringBuilder.append(str.charAt(index));
                if ((int) str.charAt(index) == 123) {
                    ++num9;
                } else if ((int) str.charAt(index) == 125) {
                    --num9;
                }
                ++index;
            } while (num9 > 0);
            list.add(new SubEntity(num7, num8, toEntityLook(stringBuilder.toString())));
        }
        return new EntityLook(bonesId, Arrays.asList(numArray1), Arrays.asList(numArray2), Arrays.asList(numArray3), list);
    }

    public static String convertToString(SubEntity subEntity) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(subEntity.bindingPointCategory);
        stringBuilder.append("@");
        stringBuilder.append(subEntity.bindingPointIndex);
        stringBuilder.append("=");
        stringBuilder.append(convertToString(subEntity.subEntityLook));
        return stringBuilder.toString();
    }
}
