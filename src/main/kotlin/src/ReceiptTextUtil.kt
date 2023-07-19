package src

import java.util.regex.Pattern
import java.util.stream.Collectors
import kotlin.math.ceil

/**
 * Утилита для работы с текстом чека.
 *
 * Редактирует текст для печати на чековой ленте.
 */
class ReceiptTextUtil {
    companion object {
        /**
         * Выравнивание текста по центру чека
         *
         * @param rawString
         * Исходный текст в виде строки, который надо преобразовать
         *
         * @param symbolsPerLine
         * Желаемое количество символов в строке (ширина чековой ленты в символах)
         *
         * @return
         * Отформатированный текст, разбитый по строкам
         */
        private fun alignToCenter(rawString: String, symbolsPerLine: Int): List<String> {
            val charMutableList = rawString.toMutableList()
            if (rawString.length > symbolsPerLine) {
                // сначала удалим лишние пробелы
                val editedRawString = forceDeleteAllExcessSpaces(rawString)

                // если это не помогло, то начинаем дробить строку на подстроки и выравнивать их
                if (editedRawString.length > symbolsPerLine) {
                    return splitTextToLines(
                        listOf(editedRawString),
                        symbolsPerLine
                    ).stream()
                        .flatMap { alignToCenter(it, symbolsPerLine).stream() }
                        .collect(Collectors.toList())
                }

                return alignToCenter(editedRawString, symbolsPerLine)
            } else if (rawString.length < symbolsPerLine) {

                var additionalSpacesNumber = symbolsPerLine - rawString.length
                var flag = true
                while (additionalSpacesNumber != 0) {
                    if (charMutableList.size != symbolsPerLine) {
                        if (flag) {
                            charMutableList.add(0, ' ')
                        } else {
                            charMutableList.add(charMutableList.lastIndex + 1, ' ')
                        }
                        flag = !flag
                        additionalSpacesNumber--
                    }
                }
            }
            return listOf(String(charMutableList.toCharArray()))
        }

        private fun fillMiddleSpaces(rawString: String, symbolsPerLine: Int): List<String> {
            var result = listOf<String>()

            if (rawString.contains(Pattern.compile("\\s{2,}").toRegex())) {
                val pattern = Pattern.compile("\\s{2,}").toRegex()
                var sizeOfFoundPattern = 0
                var indexOfPatternStart = 0

                pattern.findAll(rawString).forEach { f ->
                    sizeOfFoundPattern = (f.range.last - f.range.first) + 1
                    indexOfPatternStart = f.range.first
                }

                // если найденный паттерн занимает >= 25% длины исходной строки, то растянем его
                if (sizeOfFoundPattern >= ceil(rawString.length * 0.25)) {
                    val chars = rawString.toMutableList()

                    while (chars.size < symbolsPerLine) {
                        chars.add(indexOfPatternStart, ' ') // растягиваем
                    }

                    result = listOf(String(chars.toCharArray()))
                }
            }

            return result
        }

        /**
         * Выравнивание текста по правому краю чека
         *
         * @param rawString
         * Исходный текст в виде строки, который надо преобразовать
         *
         * @param symbolsPerLine
         * Количество символов в строке (ширина чековой ленты в символах)
         *
         * @return
         * Отформатированный текст, разбитый на лист строк
         */
        private fun alignToRight(rawString: String, symbolsPerLine: Int): List<String> {
            val charMutableList = rawString.toMutableList()

            if (rawString.length > symbolsPerLine) {
                // сначала удалим лишние пробелы
                val editedRawString = deleteExcessSpaces(rawString, symbolsPerLine)

                // если это не помогло, то начинаем дробить строку на подстроки и выравнивать их
                if (editedRawString.length > symbolsPerLine) {
                    return splitTextToLines(
                        listOf(editedRawString),
                        symbolsPerLine
                    ).stream()
                        .flatMap { alignToRight(it, symbolsPerLine).stream() }
                        .collect(Collectors.toList())
                }
                return listOf(editedRawString)
            } else if (rawString.length < symbolsPerLine) {

                // случай, когда строка короткая, но имеет кучу пробелов (просто дополним разрыв из пробелов)
                val appendedString = fillMiddleSpaces(rawString, symbolsPerLine)
                if (appendedString.isNotEmpty()) {
                    return appendedString
                }

                var additionalSpacesNumber = symbolsPerLine - rawString.length
                while (additionalSpacesNumber != 0) {
                    if (charMutableList.size != symbolsPerLine) {
                        charMutableList.add(0, ' ')
                        additionalSpacesNumber--
                    }
                }

            }
            return listOf(String(charMutableList.toCharArray()))
        }

        /**
         * Выравнивание текста по левому краю чека
         *
         * @param rawString
         * Исходный текст в виде строки, который надо преобразовать
         *
         * @param symbolsPerLine
         * Количество символов в строке (ширина чековой ленты в символах)
         *
         * @return
         * Отформатированный текст, разбитый на лист строк
         */
        private fun alignToLeft(rawString: String, symbolsPerLine: Int): List<String> {
            val charMutableList = rawString.toMutableList()

            if (rawString.length > symbolsPerLine) {
                // сначала удалим лишние пробелы
                val editedRawString = deleteExcessSpaces(rawString, symbolsPerLine)

                // если это не помогло, то начинаем дробить строку на подстроки и выравнивать их
                if (editedRawString.length > symbolsPerLine) {
                    return splitTextToLines(
                        listOf(editedRawString),
                        symbolsPerLine
                    ).stream()
                        .flatMap { alignToLeft(it, symbolsPerLine).stream() }
                        .collect(Collectors.toList())
                }
                return listOf(editedRawString)
            } else if (rawString.length < symbolsPerLine) {

                // случай, когда строка короткая, но имеет кучу пробелов (просто дополним разрыв из пробелов)
                val appendedString = fillMiddleSpaces(rawString, symbolsPerLine)
                if (appendedString.isNotEmpty()) {
                    return appendedString
                }

                var additionalSpacesNumber = symbolsPerLine - rawString.length
                while (additionalSpacesNumber != 0) {
                    if (charMutableList.size != symbolsPerLine) {
                        charMutableList.add(charMutableList.lastIndex + 1, ' ')
                        additionalSpacesNumber--
                    }
                }
            }
            return listOf(String(charMutableList.toCharArray()))
        }

        /**
         * Разбивает длинные строки на более короткие
         * с сохранением форматирования
         *
         * Применяется если исходный чек по ширине больше желаемой
         *
         * @param rawStringMutableList
         * Исходный текст, разбитый на строки
         *
         * @param element
         * Уже преобразованный текст строки с которой мы сейчас работаем
         *
         * @param symbolsPerLine
         * Желаемое количество символов в строке (ширина чековой ленты в символах)
         *
         * @param iterator
         * Номер строки с которой мы сейчас работаем
         */
        private fun splitLongLine(
            rawStringMutableList: MutableList<String>,
            element: String,
            symbolsPerLine: Int,
            iterator: Int,
        ) {
            val currentRowContent: String
            if (element.length > symbolsPerLine) {
                // определяем, что будет оставлено на текущей строке
                currentRowContent =
                    if ((element[symbolsPerLine - 1].isLetter() || element[symbolsPerLine - 1].isDigit()) && !element[symbolsPerLine].isWhitespace()) {
                        val splittingPosition = (element.substring(0, symbolsPerLine - 1)).indexOfLast { it == ' ' }
                        if (splittingPosition == -1) {
                            element.substring(0, symbolsPerLine)
                        } else {
                            element.substring(0, splittingPosition)
                        }
                    } else {
                        element.substring(0, symbolsPerLine)
                    }

                // переносим всё что не влезло в эту строку на следующую
                rawStringMutableList[iterator] = currentRowContent.trim()
                rawStringMutableList.add(iterator + 1, element.substring(currentRowContent.length).trim())
            } else {
                rawStringMutableList[iterator] = element
            }
        }

        /**
         * Выравнивание текста по ширине чека. Текст будет растянут (дополнен пробелами)
         *
         * @param rawStringMutableList
         * Исходный текст, разбитый на строки
         *
         * @param symbolsPerLine
         * Желаемое количество символов в строке (ширина чековой ленты в символах)
         *
         * @return
         * Отформатированный текст, разбитый на лист строк
         */
        private fun justify(rawStringMutableList: MutableList<String>, symbolsPerLine: Int): List<String> {
            var i = 0 // элемент массива с которым мы работаем (итератор)
            while (i <= rawStringMutableList.lastIndex) {
                var element = rawStringMutableList[i].trim()

                // Делим текст по строкам, длина строк ограничена шириной ленты
                //-----------------------------------------------------------------
                if (element.length > symbolsPerLine) {

                    // Первым делом равномерно удаляем лишние пробелы, если они есть
                    element = forceDeleteAllExcessSpaces(element)
                    // element = findAndDeleteExcessSpaces(element, symbolsPerLine)

                    // Попадаем сюда, если лишние пробелы кончились
                    // если элемент не влезает на строку, то будем искать последнее слово, которое входит в текущую строку,
                    // чтобы поставить перенос перед ним
                    splitLongLine(rawStringMutableList, element, symbolsPerLine, i)
                }
                i++
            }

            // растягиваем строку по ширине
            for (j in 0 until rawStringMutableList.size) {
                if (
                    rawStringMutableList[j].contains(" ")
                    &&
                    rawStringMutableList[j].length > ceil(symbolsPerLine * 0.6)
                ) {
                    val element = rawStringMutableList[j].toMutableList()

                    var ptr = 0
                    while (element.size < symbolsPerLine) {
                        if (element[ptr] == ' ') {
                            element.add(ptr, ' ')
                            ptr += 2
                        } else ptr++

                        if (ptr >= element.size - 1)
                            ptr = 0
                    }
                    val sb = StringBuilder()
                    element.stream().forEach { sb.append("$it") }
                    rawStringMutableList[j] = sb.toString()
                }
                // случай, когда строка короткая, но имеет кучу пробелов (просто дополним разрыв из пробелов)
                else if (rawStringMutableList[j].contains(Pattern.compile("\\s{2,}").toRegex())) {
                    val pattern = Pattern.compile("\\s{2,}").toRegex()
                    val r = pattern.findAll(rawStringMutableList[j])

                    var sizeOfFoundPattern = 0
                    var indexOfPatternStart = 0
                    r.forEach { f ->
                        sizeOfFoundPattern = (f.range.last - f.range.first) + 1
                        indexOfPatternStart = f.range.first
                    }

                    // если найденный паттерн занимает >= 25% длины исходной строки, то растянем его
                    if (sizeOfFoundPattern >= ceil(rawStringMutableList[j].length * 0.25)) {
                        val element = rawStringMutableList[j].toMutableList()

                        while (element.size < symbolsPerLine) {
                            element.add(indexOfPatternStart, ' ') // растягиваем
                        }
                        val sb = StringBuilder()
                        element.stream().forEach { sb.append("$it") }
                        rawStringMutableList[j] = sb.toString()
                    }
                } else if (Pattern.compile("(.)\\1+").matcher(rawStringMutableList[j]).matches()) {
                    rawStringMutableList[j] = rawStringMutableList[j].padEnd(symbolsPerLine, rawStringMutableList[j][0])
                } else {
                    // дополним пробелами до ширины строки
                    rawStringMutableList[j] = rawStringMutableList[j].padEnd(symbolsPerLine)
                }
            }
            return rawStringMutableList
        }

        /**
         * Найти участки в тексте, заполненные пробелами
         *
         * @param rawString
         * Текст
         *
         * @return
         * Мапа, где ключ - это начало участка (номер символа), а значение - это длина (кол-во символов) участка
         */
        private fun findExcessSpaces(rawString: String): Map<Int, Int> {
            val resultMap = mutableMapOf<Int, Int>()

            val pattern = Pattern.compile("\\s{2,}")
            val matcher = pattern.matcher(rawString)

            while (matcher.find()) {
                val start = matcher.start()
                val end = matcher.end()
                val size = end - start
                resultMap[start] = size
            }
            return resultMap
        }

        /**
         * Принудительно заменить в тексте все лишние пробелы на одинарные
         *
         * @param rawString
         * Исходный текст
         *
         * @return
         * Отформатированный текст
         */
        private fun forceDeleteAllExcessSpaces(rawString: String): String {
            val pattern = Pattern.compile("\\s+").toRegex()
            return rawString.replace(pattern, " ")
        }

        /**
         * Удалить в тексте все лишние пробелы.
         *
         * Метод равномерно удаляет лишние пробелы в тексте, оставляя, если это возможно,
         * исходное форматирование текста большим числом пробелов.
         *
         * @param rawString
         * Исходный текст
         *
         * @param spaceMap
         * Мапа, в которой ключ - начало участка исходного текста, состоящего только из пробелов,
         * а значение - длина такого участка.
         * Сформировать можно через метод [findExcessSpaces]
         *
         * @param symbolsPerLine
         * Желаемое количество символов в строке (ширина чековой ленты в символах)
         *
         * @return
         * Отформатированный текст
         */
        private fun evenlySpaceDeleting(rawString: String, spaceMap: Map<Int, Int>, symbolsPerLine: Int): String {
            var result: String = rawString
            val spaceMapKeys = spaceMap.keys.toIntArray()

            if (spaceMap.isNotEmpty()) {
                for ((iterator, spaceSectionStart) in spaceMapKeys.withIndex()) {
                    result = result.removeRange(spaceSectionStart - iterator, spaceSectionStart - iterator + 1)

                    if (result.length <= symbolsPerLine) break // выходим, если уменьшили длину до нужной
                }
            }
            return result
        }

        /**
         * Найти и удалить все лишние пробелы в исходном тексте
         *
         * @param rawString
         * Исходный текст
         *
         * @param symbolsPerLine
         * Количество символов в строке (ширина чековой ленты в символах)
         *
         * @return
         * Отформатированный текст
         */
        private fun deleteExcessSpaces(rawString: String, symbolsPerLine: Int): String {
            var result = rawString
            while (findExcessSpaces(result).isNotEmpty() && result.length > symbolsPerLine) {
                result = evenlySpaceDeleting(result, findExcessSpaces(result), symbolsPerLine)
            }
            return result
        }

        /**
         * Преобразование текста в строки для печати на чековой ленте.
         *
         * @param rawList
         * Текст, разбитый по абзацам, собранный в лист
         *
         * @param symbolsPerLine
         * Количество символов в строке (ширина чековой ленты в символах)
         *
         * @return
         * Отформатированный текст, разбитый на лист строк
         */
        private fun splitTextToLines(rawList: List<String>, symbolsPerLine: Int): MutableList<String> {
            val rawStringMutableList: MutableList<String> = rawList.toMutableList()
            var i = 0 // элемент массива с которым мы работаем (итератор)
            while (i <= rawStringMutableList.lastIndex) {
                val element = rawStringMutableList[i].trim()

                // Делим текст по строкам, длина строк ограничена шириной ленты
                splitLongLine(rawStringMutableList, element, symbolsPerLine, i)
                i++
            }
            return rawStringMutableList
        }

        /**
         * Удаляет все пробелы в начале строк чека
         * таким образом, чтобы весь чек, как бы, сдвинуть влево
         *
         * @param input
         * Текст, разбитый на строки
         *
         * @return
         * Отредактированный текст, разбитый на строки
         */
        private fun lTrim(input: List<String>): List<String> {
            // удаляем отступы в начале строки
            // посчитаем сколько у нас в среднем отступ в начале
            val lTrimSizes = input.stream()
                .map { it.length - it.replace(Regex("^\\s+"), "").length }
                .sorted()
                .collect(Collectors.toList())
            val medianCountOfLeftSpaces = if (lTrimSizes.size % 2 == 1) {
                lTrimSizes[lTrimSizes.size / 2]
            } else {
                (lTrimSizes[lTrimSizes.size / 2 - 1] + lTrimSizes[lTrimSizes.size / 2]) / 2
            }

            // удалим соответствующее кол-во пробелов
            if (medianCountOfLeftSpaces > 0) {
                val pattern = "^\\s{0,$medianCountOfLeftSpaces}"
                return input.stream().map { return@map it.replace(Regex(pattern), "") }.collect(Collectors.toList())
            }
            return input
        }


        /**
         * Удаляет все пробелы в конце строк чека
         * таким образом, чтобы весь чек, как бы, сдвинуть вправо
         *
         * @param input
         * Текст, разбитый на строки
         *
         * @return
         * Отредактированный текст, разбитый на строки
         */
        private fun rTrim(input: List<String>): List<String> {
            // удаляем отступы в конце строки
            // посчитаем сколько у нас в среднем отступ в конце
            val rTrimSizes = input.stream()
                .map { it.length - it.replace(Regex("\\s+$"), "").length }
                .sorted()
                .collect(Collectors.toList())
            val medianCountOfRightSpaces = if (rTrimSizes.size % 2 == 1) {
                rTrimSizes[rTrimSizes.size / 2]
            } else {
                (rTrimSizes[rTrimSizes.size / 2 - 1] + rTrimSizes[rTrimSizes.size / 2]) / 2
            }

            // удалим соответствующее кол-во пробелов в конце
            if (medianCountOfRightSpaces > 0) {
                val pattern = "\\s{0,$medianCountOfRightSpaces}$"
                return input.stream().map { return@map it.replace(Regex(pattern), "") }.collect(Collectors.toList())
            }
            return input
        }

        /**
         * Дополняет каждую строку чека пробелами до медианной длинны строки,
         *
         * Применяется для того чтобы все строки в чеки были одинаковой длины.
         * Это упрощает дальнейшую работу с ними
         *
         * @param input
         * Текст, разбитый на строки
         *
         * @return
         * Отредактированный текст, разбитый на строки
         */
        private fun padString(input: List<String>): List<String> {
            // считаем медианную длину строки
            val elementSizes = input.stream()
                .filter { it.isNotEmpty() }
                .map { it.length }
                .sorted().collect(Collectors.toList())

            val middle = elementSizes.size / 2

            val medianSizeOfElement = if (elementSizes.size % 2 == 1) {
                elementSizes[middle]
            } else {
                (elementSizes[middle - 1] + elementSizes[middle]) / 2
            }

            return input.stream().map {
                // строка - разделить, состоящая из одинаковых символов
                val matches = Pattern.compile("(.)\\1+").matcher(it).matches()
                if (matches) {
                    return@map it.padEnd(medianSizeOfElement, it[0])
                }
                // защита от обрыва
                if (it.length.toDouble() < medianSizeOfElement) {
                    return@map it.padEnd(medianSizeOfElement, ' ')
                }

                return@map it
            }.collect(Collectors.toList())
        }

        /**
         * Преобразование текста в строки для печати на чековой ленте.
         *
         * Исходный текст разбивается на строки, далее происходит форматирование для печати на
         * чековой ленте на основе следующих правил:
         * 1) Если строка начинается с пробела и заканчивается им, то она будет выровнена по центру
         * 2) Если строка начинается с пробела, но не заканчивается им, то она выравнивается по правому краю
         * 3) Если строка заканчивается пробелом, но не начинается им, то она выравнивается по левому краю
         * 4) Если строка не начинается с пробела и не заканчивается им, то она будет выровнена по ширине чековой ленты
         *    например, с помощью добавления пробелов, или переносом слов на новую строку
         *
         * @param input
         * Исходный текст
         *
         * @param symbolsPerLine
         * Желаемое количество символов в строке (ширина чековой ленты в символах)
         *
         * @return
         * Отформатированный текст, разбитый по строкам
         */
        fun textProcessing(input: String, symbolsPerLine: Int, containsReceiptCodes: Boolean = false): List<String> {
            // Предварительная обработка текста
            //-----------------------------------------------------------------
            // разбиваем текст по абзацам
            var sourceList = input.split("\r\n", "\n\r", "\n", "\r")

            sourceList = lTrim(sourceList)
            sourceList = rTrim(sourceList)
            sourceList = padString(sourceList)

            val resultDataList = mutableListOf<String>()
            // работаем с текстом в зависимости от выравнивания
            for (str in sourceList) {
                // центр
                if (str.startsWith(" ") && str.endsWith(" ")) {
                    resultDataList.addAll(alignToCenter(str, symbolsPerLine))
                }
                // правый край
                else if (str.startsWith(" ") && !str.endsWith(" ")) {
                    resultDataList.addAll(alignToRight(str, symbolsPerLine))
                }
                // левый край
                else if (!str.startsWith(" ") && str.endsWith(" ")) {
                    resultDataList.addAll(alignToLeft(str, symbolsPerLine))
                }
                // по ширине (обычный текст)
                else {
                    resultDataList.addAll(justify(mutableListOf(str), symbolsPerLine))
                }
            }

            // если в тексте чека есть стандартные коды разделения на чек клиента и чек кассира
            if (containsReceiptCodes) {
                // разобьём на два чека - чек клиента и чек кассира
                val output: MutableList<MutableList<String>> = mutableListOf()
                var k = -1
                resultDataList.forEach {
                    if (it.contains("0xD")) {
                        output.add(mutableListOf())
                        ++k
                    }
                    if (k >= 0) {
                        if (!it.contains("^^") && !it.contains("~")) {
                            output[k].add(it)
                        }
                    }
                }
                return output.stream().map { it.stream().collect(Collectors.joining("\n")) }.collect(Collectors.toList())
            }
             return resultDataList
        }
    }
}
