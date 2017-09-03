package main

import (
	"time"
	"sync"
	"math/rand"
	"bytes"
	"strconv"
	"github.com/mitchellh/colorstring"
	"math"
)


var mutex = &sync.Mutex{}
var oneFieldTravelTime = 10.0
var beginTimeConflict = 1
var toWait = beginTimeConflict
var wg sync.WaitGroup
var wireLength = 20
var startTime = time.Now()
var hostRandRestTime = 20
type frame struct {
	sourceAddress string
	destinationAddress string
	data string
	lifetime float64
	startDate time.Time
	timeMargin float64
}

type host struct {
	ip string
	wireLocationIndex int
	receivedFrames []frame
	currentWireIndex int
	messageToSend string
	wire []frame
	framesToSend []string
	color string
}


func main(){

	wire := make([]frame, wireLength)
	for i := range wire{
		wire[i] = frame{"", "", "", 0,
			time.Date(1970, 1,1,1,1,1,1, time.UTC),
			-1}
	}

	host1  := host{"host1", 0,make([]frame, 0), 0,
		"",wire,[]string{"a", "b", "c", "d"}, "cyan"}
	host2  := host{"host2", wireLength - 1,make([]frame, 0),
		wireLength -1, "", wire,[]string{"z", "y", "x", "w", "v", "u"},
		"green"}


	//[]string{"a", "b", "c", "d",	"e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"}, "cyan"}

	//[]string{"z", "y", "x", "w", "v", "u", "t", "s", "r", "q", "p", "o", "n", "m", "l", "k", "j", "i", "h", "g", "f", "e", "d", "c", "b", "a"}, "green"}

	startHost(&host1)
	startHost(&host2)

	go func () {
		releaseWire(wire)
	}()

	wg.Add(2)
	wg.Wait()
}

func (h* host) sendMessage()bool {
	sentWithoutDisruption := true
	h.currentWireIndex = h.wireLocationIndex
	colorstring.Println(addColorToString(h.color, sumStrings(h.ip, "start to sending message")))
		for  range h.wire {
				mutex.Lock()
				h.wire[h.currentWireIndex] = h.getFrame()
				mutex.Unlock()
			if h.wire[h.wireLocationIndex].data != h.messageToSend {
				colorstring.Println(addColorToString(h.color, sumStrings(h.ip,
					"detected conflict but it stream all time to be sure that other hosts would detect the conflict")))
				sentWithoutDisruption = false
			}
			//for i := range h.wire {
			//	colorstring.Println(addColorToString(h.color, sumStrings("wire[", strconv.Itoa(h.currentWireIndex),
			//		"] = ", h.wire[i].data, " ")))
			//	print("wire [", i, "] = ", h.wire[h.currentWireIndex].data, " ")
			//}
			//println()
			colorstring.Println(addColorToString(h.color, sumStrings("wire[", strconv.Itoa(h.currentWireIndex),
				"] = ", h.wire[h.currentWireIndex].data, " ")))
			h.incrementWireIndex()
			time.Sleep(time.Millisecond * time.Duration(oneFieldTravelTime))
		}
	return sentWithoutDisruption
}

func (h* host) incrementWireIndex() {
	if h.wireLocationIndex == 0 {
		h.currentWireIndex++
	}else {
		h.currentWireIndex--
	}
}

func (h host) getFrameLiveTime()float64 {
	return float64(len(h.wire)) * 2.0 * (oneFieldTravelTime / 1000.0)
}

func releaseWire(wire []frame){
	for {
		for i := range wire{
			if  time.Now().Sub(wire[i].startDate).Seconds() > wire[i].lifetime && wire[i].startDate != time.Date(1970, 1,1,1,1,1,1, time.UTC){
				mutex.Lock()
				wire[i] = frame{"", "", "", 0, time.Date(1970, 1,1,1,1,1,1, time.UTC), -1}
				mutex.Unlock()
			}
		}
		time.Sleep(time.Millisecond * time.Duration(oneFieldTravelTime/10))
	}
}

func startHost(h* host){
	go func() {
		defer wg.Done()
		s1 := rand.NewSource(time.Now().UnixNano())
		r1 := rand.New(s1)
		for{
			mutex.Lock()
			toWait = beginTimeConflict
			mutex.Unlock()
			if len(h.framesToSend) == 0 {
				h.onlyListenToMessage()
			} else {
				h.listenToMessage(r1.Intn(hostRandRestTime) + 1)
			}
			for {
				h.messageToSend = h.framesToSend[0]
				if h.sendMessage() && h.simulateWaiting(){
					colorstring.Println(addColorToString(h.color, sumStrings(h.ip, "sent message: ",h.messageToSend, "successfully" )))
					h.framesToSend = append(h.framesToSend[:0], h.framesToSend[1:]...)
					break
				}else {
					colorstring.Println(addColorToString(h.color, sumStrings(h.ip, "detected conflict")))
					h.waitUntilWireBusy()
					h.handleConflict()
				}
			}
		}
	}()
}
func (h* host) handleConflict(){
	s1 := rand.NewSource(time.Now().UnixNano())
	r1 := rand.New(s1)
	currentWait := 0
	if r1.Intn(2) == 0 {
		currentWait = r1.Intn(toWait) + 1
		colorstring.Println(addColorToString(h.color, sumStrings(h.ip, "will wait",
			strconv.Itoa(currentWait), "seconds before streaming to resolve conflict")))
		h.listenToMessage(currentWait)
		if toWait > int(math.Pow(2, 15)) {
			mutex.Lock()
			toWait = beginTimeConflict
			mutex.Unlock()
		} else{
			mutex.Lock()
			toWait *= 2
			mutex.Unlock()
		}
	}else {
		colorstring.Println(addColorToString(h.color, sumStrings(h.ip, "start streaming")))
	}
}

func (h* host)simulateWaiting() bool{
	t := len(h.wire)
	for j := 0; j < t; j++ {
		if h.wire[h.wireLocationIndex].data != h.messageToSend{
			return false
		}
		time.Sleep(time.Millisecond * time.Duration(oneFieldTravelTime))
	}
	return true
}

func (h host) waitUntilWireBusy() {
	colorstring.Println(addColorToString(h.color, sumStrings(h.ip, "wait until the wire will be quiet")))
	for {
		if h.wire[h.wireLocationIndex].data == "" {
			break
		}
		time.Sleep(time.Millisecond * time.Duration(oneFieldTravelTime))
	}
}

func (h* host) listenToMessage(t int) {
	colorstring.Println(addColorToString(h.color, sumStrings(h.ip, "will wait for receive message ", strconv.Itoa(t), "seconds")))
	for j := 0; j < t; j++ {
		if h.wire[h.wireLocationIndex].data != "" && h.wire[h.wireLocationIndex].data != h.messageToSend{
			colorstring.Println(addColorToString(h.color, sumStrings(h.ip, "received successfully message: ", h.wire[h.wireLocationIndex].data)))
			h.receivedFrames = append(h.receivedFrames, h.wire[h.wireLocationIndex] )
			break
		}
		time.Sleep(time.Millisecond * time.Duration(oneFieldTravelTime))
	}
	for h.wire[h.wireLocationIndex].data != "" {
		time.Sleep(time.Millisecond * time.Duration(oneFieldTravelTime))
	}
}

func (h* host) onlyListenToMessage() {
	colorstring.Println(addColorToString(h.color, sumStrings(h.ip, "dont have anything more to send, he will listen only")))
	println(h.ip, "finished work in time", time.Now().Sub(startTime).Seconds(),"seconds")
	for {
		if h.wire[h.wireLocationIndex].data != "" && h.wire[h.wireLocationIndex].data != h.messageToSend{
			colorstring.Println(addColorToString(h.color, sumStrings(h.ip, "received successfully message: ", h.wire[h.wireLocationIndex].data)))
			h.receivedFrames = append(h.receivedFrames, h.wire[h.wireLocationIndex] )
		}
		time.Sleep(time.Millisecond * time.Duration(oneFieldTravelTime))
	}
}

func addColorToString (color string, s string) string{
	var buffer bytes.Buffer

	buffer.WriteString("[")
	buffer.WriteString(color)
	buffer.WriteString("]")
	buffer.WriteString(s)
	return buffer.String()
}

func sumStrings(nums ...string)string {

	var buffer bytes.Buffer
	for _, s := range nums {
		buffer.WriteString(s)
		buffer.WriteString(" ")
	}
	return buffer.String()
}

func (h host) getFrame() frame {
	return frame{h.ip + h.wire[h.currentWireIndex].sourceAddress,
		     "dawdawd" +  h.wire[h.currentWireIndex].destinationAddress,h.messageToSend + h.wire[h.currentWireIndex].data,
		     h.getFrameLiveTime(), time.Now(), 1}
}